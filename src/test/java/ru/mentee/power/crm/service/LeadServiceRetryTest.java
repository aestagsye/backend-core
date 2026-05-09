package ru.mentee.power.crm.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 8089)
@Transactional
class LeadServiceRetryTest {

  @Autowired private LeadService leadService;

  @Test
  void shouldNotRetry_whenServerReturnsInternalServerError_andUseFallback() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Service Unavailable")));

    Lead created =
        leadService.createLead(
            "unique1@test.com", new Company("Retry Company", "IT"), LeadStatus.NEW);

    assertThat(created).isNotNull();
    assertThat(created.getEmail()).isEqualTo("unique1@test.com");

    verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldFailAfterAllRetries_whenTimeoutPersists_andUseFallback() {
    stubFor(get(urlPathEqualTo("/api/validate/email")).willReturn(ok().withFixedDelay(10000)));

    Lead created =
        leadService.createLead(
            "unique2@test.com", new Company("Retry Company", "IT"), LeadStatus.NEW);

    assertThat(created).isNotNull();
    verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldNotRetry_whenClientErrorOccurs_andUseFallback() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(badRequest().withBody("{\"error\": \"Invalid format\"}")));

    Lead created =
        leadService.createLead(
            "unique3@test.com", new Company("Retry Company", "IT"), LeadStatus.NEW);

    assertThat(created).isNotNull();
    verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldRetry_whenTimeoutOccurs_andSucceed() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(ok().withFixedDelay(10000))
            .willSetStateTo("After Timeout"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs("After Timeout")
            .willReturn(
                okJson("{\"email\": \"test@example.com\", \"valid\": true, \"reason\": \"OK\"}")));

    Lead created =
        leadService.createLead(
            "unique4@test.com", new Company("Retry Company", "IT"), LeadStatus.NEW);

    assertThat(created).isNotNull();
    verify(2, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }
}
