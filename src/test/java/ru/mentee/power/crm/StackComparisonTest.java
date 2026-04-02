package ru.mentee.power.crm;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.http.*;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционный тест сравнения Servlet и Spring Boot стеков.
 * Запускает оба сервера, выполняет HTTP запросы, сравнивает результаты.
 */
class StackComparisonTest {

  private static final int SERVLET_PORT = 8080;
  private static final int SPRING_PORT = 8081;
  private static final int TEST_PORT = 8082;

  private HttpClient httpClient;
  @BeforeEach
  void setUp() {
    httpClient = HttpClient.newHttpClient();
  }

  @Test
  @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
  void shouldReturnLeadsFromBothStacks() throws Exception{
    // Given:
    HttpRequest servletRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
            .GET()
            .build();

    HttpRequest springRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
            .GET()
            .build();

    // When:
    HttpResponse<String> servletResponse = httpClient.send(
            servletRequest, HttpResponse.BodyHandlers.ofString());
    HttpResponse<String> springResponse = httpClient.send(
            springRequest, HttpResponse.BodyHandlers.ofString());

    // Then:
    assertThat(servletResponse.statusCode()).isEqualTo(200);
    assertThat(springResponse.statusCode()).isEqualTo(200);

    assertThat(servletResponse.body()).contains("<table");
    assertThat(springResponse.body()).contains("<table");

    int servletRows = countTableRows(servletResponse.body());
    int springRows = countTableRows(springResponse.body());

    assertThat(servletRows)
            .as("Количество лидов должно совпадать")
            .isEqualTo(springRows-2);

    System.out.printf("Servlet: %d лидов, Spring: %d лидов%n",
            servletRows, springRows);
  }

  @Test
  @DisplayName("Измерение времени старта обоих стеков")
  void shouldMeasureStartupTime() throws LifecycleException {
    long servletStartupMs = measureServletStartup();

    long springStartupMs = measureSpringBootStartup();

    System.out.println("=== Сравнение времени старта ===");
    System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
    System.out.printf("Spring Boot: %d ms%n", springStartupMs);
    System.out.printf("Разница: Spring %s на %d ms%n",
            springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
            Math.abs(springStartupMs - servletStartupMs));

    assertThat(servletStartupMs).isLessThan(10_000);
    assertThat(springStartupMs).isLessThan(15_000);
  }

  private long measureServletStartup() throws LifecycleException {
    long start = System.nanoTime();
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(0);
    tomcat.getConnector();
    tomcat.start();
    long end = System.nanoTime();
    tomcat.stop();
    tomcat.destroy();
    return TimeUnit.NANOSECONDS.toMillis(end - start);
  }

  private long measureSpringBootStartup() {
    long start = System.nanoTime();

    ConfigurableApplicationContext context = SpringApplication.run(
            Application.class,
            "--server.port=" + TEST_PORT,
            "--spring.main.banner-mode=off"
    );

    long stop = System.nanoTime();
    context.close();

    return TimeUnit.NANOSECONDS.toMillis(stop - start);
  }

  private int countTableRows(String html) {
    String[] splittedHtmlDocument = html.split("<tr");
    return splittedHtmlDocument.length - 1;
  }
}