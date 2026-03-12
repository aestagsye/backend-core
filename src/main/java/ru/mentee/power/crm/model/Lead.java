package ru.mentee.power.crm.model;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record Lead(
        UUID id,
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Некорректный формат email")
        String email,
        @NotBlank(message = "Company обязателен")
        String company,
        @NotNull(message = "Статус обязателен")
        LeadStatus status
) {
}