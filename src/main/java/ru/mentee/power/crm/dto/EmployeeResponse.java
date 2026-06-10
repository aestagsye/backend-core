package ru.mentee.power.crm.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EmployeeResponse(UUID id, String name, BigDecimal salary) {}
