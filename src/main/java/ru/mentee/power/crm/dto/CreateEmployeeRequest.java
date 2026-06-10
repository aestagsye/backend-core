package ru.mentee.power.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateEmployeeRequest(@NotBlank String name, @NotNull @Positive BigDecimal salary) {}
