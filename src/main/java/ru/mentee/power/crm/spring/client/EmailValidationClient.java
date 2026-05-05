package ru.mentee.power.crm.spring.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailValidationClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public EmailValidationClient(
      RestTemplate restTemplate, @Value("${email.validation.base-url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public EmailValidationResponse validateEmail(String email) {
    String url = baseUrl + "/api/validate/email?email=" + email;

    try {
      return restTemplate.getForObject(url, EmailValidationResponse.class);
    } catch (HttpServerErrorException e) {
      return new EmailValidationResponse(email, false, "Email validation service unavailable");
    } catch (RestClientException e) {
      throw new RuntimeException();
    }
  }
}
