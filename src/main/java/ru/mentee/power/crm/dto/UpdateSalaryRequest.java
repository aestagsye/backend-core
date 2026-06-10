package ru.mentee.power.crm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateSalaryRequest(@NotNull @Positive BigDecimal salary) {}
