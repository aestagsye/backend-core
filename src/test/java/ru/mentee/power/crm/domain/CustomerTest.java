package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustomerTest {

  @Test
  void shouldReuseContact_whenCreatingCustomer() {
    Address address1 = new Address("Ulan-Ude", "Lenina", "670000");
    Address billingAddress = new Address("Moscow", "Volokolomskaya", "450647");
    Contact contact = new Contact("ezio@auditore.dafirenze", "+712", address1);

    Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "GOLD");

    assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
  }

  @Test
  void shouldDemonstrateContactReuse_acrossLeadAndCustomer() {
    Address address = new Address("Ulan-Ude", "Lenina", "670000");
    Address billingAddress = new Address("Moscow", "Volokolomskaya", "450647");
    Contact contact = new Contact("ezio@auditore.dafirenze", "+791", address);

    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, contact, "ACB", "QUALIFIED");
    UUID id1 = UUID.randomUUID();
    Customer customer = new Customer(id1, contact, billingAddress, "GOLD");
    assertThat(lead.contact()).isSameAs(customer.contact());
  }

  @Test
  void shouldThrowException_wheneverSomethingIsInvalid() {
    Address address1 = new Address("Ulan-Ude", "Lenina", "670000");
    Address billingAddress = new Address("Moscow", "Volokolomskaya", "450647");
    Contact contact = new Contact("ezio@auditore.dafirenze", "+712", address1);
    UUID id = UUID.randomUUID();

    assertThatThrownBy(() -> new Customer(null, contact, billingAddress, "GOLD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id cannot be null");
    assertThatThrownBy(() -> new Customer(id, null, billingAddress, "GOLD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Contact cannot be null");
    assertThatThrownBy(() -> new Customer(id, contact, null, "GOLD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("billingAddress cannot be null");
    assertThatThrownBy(() -> new Customer(id, contact, billingAddress, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("loyaltyTier cannot be null or empty");
    assertThatThrownBy(() -> new Customer(id, contact, billingAddress, "bruh"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("loyaltyTier is invalid");
  }
}