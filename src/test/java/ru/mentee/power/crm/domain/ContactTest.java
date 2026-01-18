package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContactTest {
  @Test
  void shouldCreateContact_whenValidData() {
    Contact contact = new Contact("John", "Pork", "pork@porkchop.com");

    String firstName = contact.firstName();
    String lastName = contact.lastName();
    String email = contact.email();

    assertThat(firstName).isEqualTo("John");
    assertThat(lastName).isEqualTo("Pork");
    assertThat(email).isEqualTo("pork@porkchop.com");
  }
}