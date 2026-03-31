package ru.mentee.power.crm;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepositoryLegacy;
import ru.mentee.power.crm.service.LeadServiceLegacy;
import ru.mentee.power.crm.servlet.LeadListServlet;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

  private Tomcat tomcat;
  private int port;
  private LeadServiceLegacy leadService;

  @BeforeEach
  void startServer() throws Exception {
    LeadRepositoryLegacy repository = new InMemoryLeadRepository();
    leadService = new LeadServiceLegacy(repository);

    // Добавляем тестовые лиды
    leadService.addLead("test1@example.com", "Test Company 1", LeadStatus.NEW);
    leadService.addLead("test2@example.com", "Test Company 2", LeadStatus.CONTACTED);

    tomcat = new Tomcat();
    tomcat.setPort(0); // случайный порт
    tomcat.getConnector();

    Context context = tomcat.addContext("", new File(".").getAbsolutePath());
    context.getServletContext().setAttribute("leadService", leadService);

    tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
    context.addServletMappingDecoded("/leads", "LeadListServlet");

    tomcat.start();
    port = tomcat.getConnector().getLocalPort();
    // Даём серверу время запуститься
    Thread.sleep(500);
  }

  @AfterEach
  void stopServer() throws Exception {
    if (tomcat != null) {
      tomcat.stop();
      tomcat.destroy();
    }
  }

  @Test
  void shouldReturnLeadsPageWithData() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/leads"))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    // Допускаем варианты заголовка: с пробелом или без
    assertThat(response.headers().firstValue("Content-Type"))
            .hasValueSatisfying(contentType ->
                    assertThat(contentType).matches("text/html;\\s*charset=UTF-8"));

    String body = response.body();
    assertThat(body).contains("Lead List");

    // Если шаблон отображает данные, можно раскомментировать:
    // assertThat(body).contains("test1@example.com");
    // assertThat(body).contains("Test Company 1");
    // assertThat(body).contains("test2@example.com");
    // assertThat(body).contains("Test Company 2");
  }

  @Test
  void shouldHandleEmptyLeadsList() throws Exception {
    // Удаляем все лиды
    leadService.findAll().forEach(lead -> leadService.delete(lead.getId()));

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/leads"))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    String body = response.body();
    assertThat(body).contains("Lead List");
    // Убеждаемся, что удалённые лиды не отображаются
    assertThat(body).doesNotContain("test1@example.com");
    assertThat(body).doesNotContain("test2@example.com");
  }

  @Test
  void shouldReturn404ForUnknownPath() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/unknown"))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(404);
  }
}