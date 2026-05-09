package ru.mentee.power.crm.spring.restexception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.mapper.LeadMapperImpl;
import ru.mentee.power.crm.spring.rest.LeadRestController;

@WebMvcTest(LeadRestController.class)
@Import(LeadMapperImpl.class)
class GlobalRestExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService service;

  @Test
  void shouldReturn404_whenEntityNotFound() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    when(service.getLeadById(id)).thenThrow(new EntityNotFoundException("Lead", id.toString()));
    // when,then
    mockMvc
        .perform(get("/api/leads/{leadId}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Lead with id " + id.toString() + " not found"))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id.toString()));
  }

  @Test
  void shouldReturn400WithFieldErrors_whenValidationFails() throws Exception {
    // given
    String jsonBody =
        """
              {
                "email": "",
                "companyName": " "
              }
            """;
    // when,then
    mockMvc
        .perform(post("/api/leads").content(jsonBody).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").isNotEmpty())
        .andExpect(jsonPath("$.errors.email").exists())
        .andExpect(jsonPath("$.errors.companyName").exists());
  }

  @Test
  void shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.getLeadById(id)).thenThrow(new RuntimeException("UnexpectedExceptionOccurred"));
    mockMvc
        .perform(get("/api/leads/{leadId}", id))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(
            jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id.toString()));
  }
}
