package ru.mentee.power.crm.exception;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.LeadStatus;

import static org.assertj.core.api.Assertions.assertThat;

class IllegalLeadStateExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage() {
    UUID leadId = UUID.randomUUID();
    LeadStatus status = LeadStatus.NEW;

    IllegalLeadStateException exception = new IllegalLeadStateException(leadId, status);

    assertThat(exception.getMessage())
            .contains("cannot be converted")
            .contains(leadId.toString())
            .contains("NEW");
    assertThat(exception.getLeadId()).isEqualTo(leadId);
    assertThat(exception.getCurrentStatus()).isEqualTo(status);
  }
}
