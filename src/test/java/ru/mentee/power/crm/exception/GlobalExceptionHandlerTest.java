package ru.mentee.power.crm.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.mentee.power.crm.domain.LeadStatus;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void shouldHandleIllegalLeadStateException() {
    UUID leadId = UUID.randomUUID();
    IllegalLeadStateException ex = new IllegalLeadStateException(leadId, LeadStatus.NEW);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    RedirectView result = handler.handleIllegalLeadState(ex, redirectAttributes);

    assertThat(result.getUrl()).isEqualTo("/leads");
    verify(redirectAttributes)
        .addFlashAttribute(eq("errorMessage"), contains("Лид должен быть в статусе QUALIFIED"));
  }

  @Test
  void shouldHandleIllegalArgumentException() {
    IllegalArgumentException ex = new IllegalArgumentException("Lead not found");
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

    RedirectView result = handler.handleIllegalArgument(ex, redirectAttributes);

    assertThat(result.getUrl()).isEqualTo("/leads");
    verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), eq("Lead not found"));
  }
}
