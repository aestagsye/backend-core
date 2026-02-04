package ru.mentee.power.crm.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HelloCrmServerTest {

  private HelloCrmServer server;
  private int port = 8081;

  @BeforeEach
  void setUp() throws IOException {
    server = new HelloCrmServer(port);
    server.start();
    try {
      Thread.sleep(500);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    }
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop();
    }
  }

  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void serverShouldRespondOnHelloEndpoint() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/hello"))
            .GET()
            .timeout(Duration.ofSeconds(3))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("text/html; charset=UTF-8", response.headers().firstValue("Content-Type").orElse(""));
    assertTrue(response.body().contains("<h1>Hello CRM!</h1>"));
    assertTrue(response.body().contains("<html>"));
    assertTrue(response.body().contains("</body>"));
  }

  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void serverShouldReturn404ForUnknownPaths() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/unknown"))
            .GET()
            .timeout(Duration.ofSeconds(3))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(404, response.statusCode());
  }

  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void serverShouldHandleMultipleRequests() throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    for (int i = 0; i < 3; i++) {
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:" + port + "/hello"))
              .GET()
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      assertEquals(200, response.statusCode());
      assertTrue(response.body().contains("Hello CRM!"));
    }
  }

  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void serverShouldSupportDifferentHttpMethods() throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/hello"))
            .GET()
            .build();

    HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
    assertEquals(200, getResponse.statusCode());

    HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/hello"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

    HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
    assertEquals(200, postResponse.statusCode());
  }
}