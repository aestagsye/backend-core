package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Customer(UUID id, Contact contact, Address billingAddress, String loyaltyTier) {
  public Customer {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null");
    }

    if (contact == null) {
      throw new IllegalArgumentException("Contact cannot be null");
    }

    if (billingAddress == null){
      throw new IllegalArgumentException("billingAddress cannot be null");
    }

    if (loyaltyTier == null || loyaltyTier.isBlank()){
      throw new IllegalArgumentException("loyaltyTier cannot be null or empty");
    }

    if (!loyaltyTier.equals("BRONZE") && !loyaltyTier.equals("SILVER") && !loyaltyTier.equals("GOLD")) {
      throw new IllegalArgumentException("loyaltyTier is invalid");
    }
  }
}