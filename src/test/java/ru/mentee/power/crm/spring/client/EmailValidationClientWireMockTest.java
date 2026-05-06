package ru.mentee.power.crm.spring.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class EmailValidationClientWireMockTest {

  @RegisterExtension
  static WireMockExtension wireMock =
      WireMockExtension.newInstance()
          .options(wireMockConfig().dynamicPort())
          .configureStaticDsl(true)
          .build();

  @Autowired private EmailValidationFeignClient emailValidationClient;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("email.validation.base-url", wireMock::baseUrl);
  }

  @Test
  void shouldReturnValid_whenEmailIsCorrect() {
    // Given: WireMock stub возвращает valid=true
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("john@example.com"))
            .willReturn(
                okJson(
                    """
                {
                    "email": "john@example.com",
                    "valid": true,
                    "reason": "Email exists"
                }
                """)));

    // When: вызываем клиент
    EmailValidationResponse response = emailValidationClient.validateEmail("john@example.com");

    // Then: получаем корректный response
    assertThat(response).isNotNull();
    assertThat(response.valid()).isTrue();
    assertThat(response.email()).isEqualTo("john@example.com");
  }

  @Test
  void shouldReturnInvalid_whenEmailIsIncorrect() {
    // Given: WireMock stub возвращает valid=false
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("invalid-email"))
            .willReturn(
                okJson(
                    """
                {
                    "email": "invalid-email",
                    "valid": false,
                    "reason": "Invalid email format"
                }
                """)));

    // When: вызываем клиент
    EmailValidationResponse response = emailValidationClient.validateEmail("invalid-email");

    // Then: email невалиден
    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
  }

  @Test
  void shouldHandleServerError_whenExternalServiceFails() {
    // Given: WireMock stub возвращает 500 Internal Server Error
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Internal Server Error")));

    // When/Then: низкоуровневый Feign-клиент пробрасывает ошибку сервера
    assertThatThrownBy(() -> emailValidationClient.validateEmail("some-email"))
        .isInstanceOf(FeignException.InternalServerError.class);
  }

  @Test
  void shouldHandleTimeout_whenExternalServiceIsSlow() {
    // Given: WireMock stub отвечает с задержкой 15 секунд (больше timeout)
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(okJson("{\"valid\": true}").withFixedDelay(15000))); // 15 секунд

    // When/Then: RestTemplate должен выбросить timeout exception
    assertThatThrownBy(() -> emailValidationClient.validateEmail("slow@example.com"))
        .isInstanceOf(RuntimeException.class);
  }
}
