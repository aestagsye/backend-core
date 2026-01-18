package ru.mentee.power.crm.domain;

public record Address(String city, String street, String zip) {

  public Address {
    // Валидация city
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City cannot be null or empty");
    }

    // Валидация zip
    if (zip == null || zip.isBlank()) {
      throw new IllegalArgumentException("Zip code cannot be null or empty");
    }
  }
}