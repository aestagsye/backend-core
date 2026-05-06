package ru.mentee.power.crm.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 8089)
class LeadServiceRetryTest {

  @Autowired private LeadService leadService;

  @Test
  void shouldNotRetry_whenServerReturnsInternalServerError() {
    // Given: 500 Internal Server Error
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Service Unavailable")));

    // When / Then: в текущей конфигурации 5xx НЕ попадает под retry-exceptions
    assertThatThrownBy(
            () ->
                leadService.createLead(
                    "test@example.com", new Company("Retry Company", "IT"), LeadStatus.NEW))
        .isInstanceOf(Exception.class);

    // Verify: только одна попытка
    verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldFailAfterAllRetries_whenTimeoutPersists() {
    // Given: все попытки завершаются timeout
    stubFor(get(urlPathEqualTo("/api/validate/email")).willReturn(ok().withFixedDelay(10000)));

    // When / Then: timeout должен ретраиться до max-attempts
    assertThatThrownBy(
            () ->
                leadService.createLead(
                    "test@example.com", new Company("Retry Company", "IT"), LeadStatus.NEW))
        .isInstanceOf(Exception.class);

    // Verify: max-attempts = 3
    verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldNotRetry_whenClientErrorOccurs() {
    // Given: 400 Bad Request (клиентская ошибка)
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(badRequest().withBody("{\"error\": \"Invalid format\"}")));

    // When/Then: исключение без retry
    // 4xx ошибки в ignore-exceptions — не повторяем
    try {
      leadService.createLead("invalid", new Company("Retry Company", "IT"), LeadStatus.NEW);
    } catch (Exception ignored) {
      // Ожидаем исключение для 4xx
    }

    // Verify: только 1 попытка — retry НЕ сработал для 4xx
    verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldRetry_whenTimeoutOccurs() {
    // Given: Первый вызов — timeout, второй — успех
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(ok().withFixedDelay(10000)) // 10 секунд — больше timeout
            .willSetStateTo("After Timeout"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs("After Timeout")
            .willReturn(
                okJson(
                    """
                {"email": "test@example.com", "valid": true, "reason": "OK"}
                """)));

    // When: создаём лида (первый вызов timeout, второй успех)
    Lead created =
        leadService.createLead(
            "test@example.com", new Company("Retry Company", "IT"), LeadStatus.NEW);

    // Then: лид создан после retry
    assertThat(created).isNotNull();

    // Verify: было 2 попытки
    verify(2, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }
}
