package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldCreateLead_whenValidData() {
    Address address = new Address("San Francisco","123 Main St","94105");
    Contact contact = new Contact("p@cop.com", "+7950", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "Abstergo", "NEW");

    assertThat(lead.contact()).isEqualTo(contact);
    assertThat(lead.company()).isEqualTo("Abstergo");
    assertThat(lead.status()).isEqualTo("NEW");
  }

  @Test
  void shouldAccessEmailThroughDelegation_whenLeadCreated() {
    Address address = new Address("San Francisco","123 Main St","94105");
    Contact contact = new Contact("p@cop.com", "+7950", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "Abstergo", "NEW");

    String email = lead.contact().email();

    assertThat(email).isEqualTo("p@cop.com");

    String city = lead.contact().address().city();

    assertThat(city).isEqualTo("San Francisco");
  }

  @Test
  void shouldBeEqual_whenSameIdButDifferentContact() {
    UUID id = UUID.randomUUID();

    Address address1 = new Address("San Francisco","123 Main St","94105");
    Contact contact1 = new Contact("p@cop.com", "+7950", address1);
    Lead lead1 = new Lead(id, contact1, "Abstergo", "NEW");

    Address address2 = new Address("San Bransiko","123 Local st","94103");
    Contact contact2 = new Contact("papa@cop.com", "+7956", address2);
    Lead lead2 = new Lead(id, contact2, "Assassins", "QUALIFIED");

    assertThat(lead1).isNotEqualTo(lead2);
  }

  @Test
  void shouldThrowException_whenContactIsNull() {
    UUID id = UUID.randomUUID();
    assertThatThrownBy(() -> new Lead(id, null, "Abstergo", "NEW"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Contact cannot be null or empty");
  }

  @Test
  void shouldThrowException_whenInvalidStatus() {
    UUID id = UUID.randomUUID();
    Address address1 = new Address("San Francisco","123 Main St","94105");
    Contact contact1 = new Contact("p@cop.com", "+7950", address1);
    assertThatThrownBy(() -> new Lead(id, contact1, "Abstergo", "INVALID"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Status is invalid");
  }

  @Test
  void shouldDemonstrateThreeLevelComposition_whenAccessingCity() {
    UUID id = UUID.randomUUID();
    Address address = new Address("San Francisco","123 Main St","94105");
    Contact contact = new Contact("p@cop.com", "+7950", address);
    Lead lead = new Lead(id, contact, "Abstergo", "NEW");

    String city = lead.contact().address().city();

    assertThat(city).isEqualTo("San Francisco");
  }
}