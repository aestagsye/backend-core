package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead(UUID id, Contact contact, String company, String status) {
  public Lead {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null");
    }

    if (contact == null) {
      throw new IllegalArgumentException("Contact cannot be null or empty");
    }

    if (status == null || status.isBlank()){
      throw new IllegalArgumentException("Status cannot be null or empty");
    }

    if (!status.equals("NEW") && !status.equals("QUALIFIED") && !status.equals("CONVERTED")) {
      throw new IllegalArgumentException("Status is invalid");
    }
  }
}