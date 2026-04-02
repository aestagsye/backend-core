package ru.mentee.power.crm.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDealRequest {
  @NotNull
  private String title;
  @Positive
  @NotNull
  private BigDecimal amount;
  @NotNull
  private UUID companyId;
}