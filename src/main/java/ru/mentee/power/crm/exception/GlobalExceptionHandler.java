package ru.mentee.power.crm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalLeadStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public RedirectView handleIllegalLeadState(
      IllegalLeadStateException ex, RedirectAttributes redirectAttributes) {
    String errorMessage =
        String.format(
            "Невозможно конвертить лид %s. Текущий статус: %s. "
                + "Лид должен быть в статусе QUALIFIED.",
            ex.getLeadId(), ex.getCurrentStatus());
    redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
    return new RedirectView("/leads");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public RedirectView handleIllegalArgument(
      IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
    return new RedirectView("/leads");
  }
}
