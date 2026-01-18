package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import ru.mentee.power.crm.storage.LeadStorage;

class LeadTest {
  @Test
  void shouldCreateLead_whenValidData() {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, "test@example.com", "+71234567890", "TestCorp", "NEW");
    assertThat(lead.id()).isEqualTo(id);
  }

  @Test
  void shouldReturnEmail_whenGetEmailCalled() {
    // Given
    Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

    // When
    String email = lead.email();

    // Then
    assertThat(email).isEqualTo("test@example.com");
  }// Допиши тесты для email, phone, company, status, toString

  @Test
  void shouldReturnPhone_whenGetPhoneCalled() {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
    String phone = lead.phone();
    assertThat(phone).isEqualTo("+71234567890");
  }

  @Test
  void shouldReturnCompany_whenGetCompanyCalled() {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
    String company = lead.company();
    assertThat(company).isEqualTo("TestCorp");
  }

  @Test
  void shouldReturnStatus_whenGetStatusCalled() {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
    String status = lead.status();
    assertThat(status).isEqualTo("NEW");
  }

  @Test
  void shouldReturnToString_whenToStringCalled() {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, "test@example.com", "+71234567890", "TestCorp", "NEW");
    String toString = lead.toString();
    assertThat(toString).isEqualTo(
            "Lead[id=" + id + ", email=test@example.com, phone=+71234567890, " +
                    "company=TestCorp, status=NEW]"
    );
  }

  @Test
  void shouldGenerateUniqueIds_whenMultipleLeads() {
    Lead lead = new Lead(UUID.randomUUID(), "p@mail.net", "+12", "new","rich");
    Lead lead1 = new Lead(UUID.randomUUID(), "p@mail.ru", "+21","old","mid");
    assertThat(lead.id()).isNotEqualTo(lead1.id());
  }

  @Test
  void shouldPreventStringConfusion_whenUsingUUID() {
    UUID leadId = UUID.randomUUID();
    Lead lead = new Lead(leadId, "test@mail.ru", "+71234567890", "Company", "NEW");

    LeadStorage storage = new LeadStorage();
    storage.add(lead);

    // Это должно работать правильно:
    Lead foundById = storage.findById(lead.id());  // OK: UUID to UUID
    assertThat(foundById).isEqualTo(lead);

    // Это не скомпилируется (демонстрация):
    // findById("some-string");  // ERROR: incompatible types

    // Но для теста мы можем проверить это через рефлексию или просто убедиться,
    // что компилятор действительно не позволит это сделать
    assertThat(lead.id()).isInstanceOf(UUID.class);
    assertThat(lead.id().toString()).isNotEqualTo("some-string");
  }
}