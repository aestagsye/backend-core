package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.dto.LeadResponse;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerValidationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService; // Mock Service слой

  @MockitoBean private LeadMapper leadMapper;

  @Test
  void shouldReturn400_whenEmailIsBlank() throws Exception {
    String requestJson =
        """
        {
          "email": "",
          "companyName": "Acme",
          "industry": "LooneyTunes",
          "status": "NEW"
        }
        """;

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400_whenEmailIsInvalidFormat() throws Exception {
    String requestJson =
        """
        {
          "email": "invalid-email",
          "companyName": "Acme",
          "industry": "LooneyTunes",
          "status": "NEW"
        }
        """;

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400_whenStatusIsNull() throws Exception {
    String requestJson =
        """
        {
          "email": "lead@example.com",
          "companyName": "Acme",
          "industry": "LooneyTunes"
        }
        """;

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn201_whenAllFieldsAreValid() throws Exception {
    String requestJson =
        """
        {
          "email": "lead@example.com",
          "companyName": "Acme",
          "industry": "LooneyTunes",
          "status": "NEW"
        }
        """;
    UUID leadId = UUID.randomUUID();
    Company company = new Company("Acme", "LooneyTunes");
    Lead mappedLead = new Lead("lead@example.com", company, LeadStatus.NEW);
    Lead savedLead =
        new Lead(leadId, "lead@example.com", company, LeadStatus.NEW, LocalDateTime.now());
    LeadResponse response =
        new LeadResponse(
            leadId, "lead@example.com", LeadStatus.NEW, "Acme", savedLead.getCreatedAt());

    when(leadMapper.toEntity(any(LeadFormDto.class))).thenReturn(mappedLead);
    when(leadService.addLead("lead@example.com", company, LeadStatus.NEW)).thenReturn(savedLead);
    when(leadMapper.toResponse(savedLead)).thenReturn(response);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/leads/" + leadId))
        .andExpect(jsonPath("$.email").value("lead@example.com"))
        .andExpect(jsonPath("$.companyName").value("Acme"))
        .andExpect(jsonPath("$.status").value("NEW"));
  }
}
