package ru.mentee.power.crm.spring.restexception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO для единообразных error responses в REST API.
 *
 * <p>Поля: - timestamp: момент возникновения ошибки - status: HTTP статус код (например 404) -
 * error: короткое название статуса (например "Not Found") - message: человекочитаемое описание
 * ошибки - path: URI к которому обращался клиент - errors: Map field errors для ошибок валидации
 * (может быть null)
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> errors) {

  /** Convenience конструктор для ошибок БЕЗ field errors. */
  public ErrorResponse(
      LocalDateTime timestamp, int status, String error, String message, String path) {
    this(timestamp, status, error, message, path, null);
  }
}
