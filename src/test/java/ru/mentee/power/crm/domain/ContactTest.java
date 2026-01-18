package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ContactTest {

  @Test
  void shouldCreateContact_whenValidData() {
    Address address = new Address("San Francisco","123 Main St","94105");
    Contact contact = new Contact("p@cop.com", "+7950", address);

    assertThat(contact.address()).isEqualTo(address);

    assertThat(contact.address().city()).isEqualTo("San Francisco");
  }

  @Test
  void shouldDelegateToAddress_whenAccessingCity() {
    Address address = new Address("San Francisco","123 Main St","94105");
    Contact contact = new Contact("p@cop.com", "+7950", address);

    assertThat(contact.address().city()).isEqualTo("San Francisco");

    assertThat(contact.address().street()).isEqualTo("123 Main St");
  }

  @Test
  void shouldThrowException_whenAddressIsNull() {
    Address address = new Address("San Francisco","123 Main St","94105");
    assertThatThrownBy(() -> new Contact("p@cop.com", "+7950", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Address cannot be null");
    assertThatThrownBy(() -> new Contact(null, "+7950", address))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email cannot be null or empty");
    assertThatThrownBy(() -> new Contact("p@cop.com", null, address))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Phone cannot be null or empty");
  }
}