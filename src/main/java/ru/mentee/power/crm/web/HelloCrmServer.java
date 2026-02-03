package ru.mentee.power.crm.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HelloCrmServer {
  private final HttpServer server;

  public HelloCrmServer(int port) throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(port), 0);
  }

  public void start() {
    server.createContext("/hello", new HelloHandler());
    server.start();
    System.out.println("Server started on http://localhost:" + server.getAddress().getPort());
  }

  public void stop() {
    server.stop(0);
  }

  static class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      // 1. Сформировать HTML строку с "Hello CRM!"
      // 2. Установить заголовок Content-Type: text/html; charset=UTF-8
      // 3. Отправить статус 200 и длину ответа
      // 4. Записать HTML в OutputStream
      // 5. Закрыть exchange
      String method = exchange.getRequestMethod();
      String path = exchange.getRequestURI().getPath();
      System.out.println("Received " + method + " request for " + path);
      String response = "<html><body><h1>Hello CRM!</h1></body></html>";
      exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
      byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, responseBytes.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(responseBytes);
      }
    }
  }
}