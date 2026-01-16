package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LeadTest {
  @Test
  void shouldReturnId_whenGetIdCalled() {
    // Given
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

    // When
    String id = lead.getId();

    // Then
    assertThat(id).isEqualTo("L1");
  }

  @Test
  void shouldReturnEmail_whenGetEmailCalled() {
    // Given
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

    // When
    String email = lead.getEmail();

    // Then
    assertThat(email).isEqualTo("test@example.com");
  }// Допиши тесты для email, phone, company, status, toString

  @Test
  void shouldReturnPhone_whenGetPhoneCalled() {
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");
    String phone = lead.getPhone();
    assertThat(phone).isEqualTo("+71234567890");
  }

  @Test
  void shouldReturnCompany_whenGetCompanyCalled() {
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");
    String company = lead.getCompany();
    assertThat(company).isEqualTo("TestCorp");
  }

  @Test
  void shouldReturnStatus_whenGetStatusCalled() {
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");
    String status = lead.getStatus();
    assertThat(status).isEqualTo("NEW");
  }

  @Test
  void shouldReturnToString_whenToStringCalled() {
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");
    String toString = lead.toString();
    assertThat(toString).isEqualTo(
            "Lead{id='L1', email='test@example.com', phone='+71234567890', " +
                    "company='TestCorp', status='NEW'}"
    );
  }
}