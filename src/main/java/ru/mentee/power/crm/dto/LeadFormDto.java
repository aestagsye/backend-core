package ru.mentee.power.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.mentee.power.crm.domain.LeadStatus;

@Data
public class LeadFormDto {
  @NotBlank @Email
  private String email;

  @NotBlank
  private String companyName;

  private String industry;

  @NotNull
  private LeadStatus status;
}