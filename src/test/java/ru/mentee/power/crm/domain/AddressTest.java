package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AddressTest {

  @Test
  void shouldCreateAddress_whenValidData() {
    Address address = new Address("San Francisco","123 Main St","94105");

    assertThat(address.city()).isEqualTo("San Francisco");
    assertThat(address.street()).isEqualTo("123 Main St");
    assertThat(address.zip()).isEqualTo("94105");
  }

  @Test
  void shouldBeEqual_whenSameData() {
    Address address1 = new Address("San Francisco","123 Main St","94105");
    Address address2 = new Address("San Francisco","123 Main St","94105");

    assertThat(address1).isEqualTo(address2);

    assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
  }

  @Test
  void shouldThrowException_whenCityIsNull() {
    // Проверяем что создание Address с city=null бросает IllegalArgumentException
    assertThatThrownBy(() -> new Address(null, "123 Main St", "94105"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("City cannot be null or empty");
  }

  @Test
  void shouldThrowException_whenZipIsBlank() {
    assertThatThrownBy(() -> new Address("San Francisco", "123 Main St", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Zip code cannot be null or empty");
  }
}