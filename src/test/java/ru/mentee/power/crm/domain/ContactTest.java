package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContactTest {

  @Test
  void shouldCreateContact_whenValidData() {
    Contact contact = new Contact("John", "Doe", "john@example.com");

    String firstName = contact.getFirstName();
    String lastName = contact.getLastName();
    String email = contact.getEmail();

    assertThat(firstName).isEqualTo("John");
    assertThat(lastName).isEqualTo("Doe");
    assertThat(email).isEqualTo("john@example.com");
  }

  @Test
  void shouldBeEqual_whenSameData() {
    Contact contact1 = new Contact("John", "Doe", "john@example.com");
    Contact contact2 = new Contact("John", "Doe", "john@example.com");

    assertThat(contact1).isEqualTo(contact2);

    assertThat(contact1.hashCode()).isEqualTo(contact2.hashCode());
  }

  @Test
  void shouldNotBeEqual_whenDifferentData() {
    Contact contact1 = new Contact("John", "Doe", "john@example.com");
    Contact contact2 = new Contact("Pork", "John", "pork@porkchop.com");

    assertThat(contact1).isNotEqualTo(contact2);
  }
}