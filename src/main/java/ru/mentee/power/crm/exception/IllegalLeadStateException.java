package ru.mentee.power.crm.exception;

import java.util.UUID;

import lombok.Getter;
import ru.mentee.power.crm.domain.LeadStatus;

@Getter
public class IllegalLeadStateException extends RuntimeException {

  private final UUID leadId;
  private final LeadStatus currentStatus;

  public IllegalLeadStateException(UUID leadId, LeadStatus currentStatus) {
    super(String.format("Lead %s cannot be converted. Current status: %s",
            leadId, currentStatus));
    this.leadId = leadId;
    this.currentStatus = currentStatus;
  }
}