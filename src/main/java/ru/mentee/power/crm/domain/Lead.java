package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead(
        UUID id,
        String email,
        String phone,
        String company,
        String status
) {

  public Lead(UUID id, String email, String phone, String company, String status) {
    this.id = id;
    this.email = email;
    this.phone = phone;
    this.company = company;
    this.status = status;
  }
}