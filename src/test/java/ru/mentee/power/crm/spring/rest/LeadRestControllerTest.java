package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.mapper.LeadMapperImpl;

@WebMvcTest(LeadRestController.class)
@Import(LeadMapperImpl.class)
class LeadRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService;

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    Company company = new Company("Acme", "Tech");
    Lead lead = new Lead("john@example.com", company, LeadStatus.NEW);
    when(leadService.findAll()).thenReturn(List.of(lead));

    mockMvc
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].email").value("john@example.com"))
        .andExpect(jsonPath("$[0].status").value("NEW"))
        .andExpect(jsonPath("$[0].companyName").value("Acme"));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.findById(id)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/leads/" + id)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID id = UUID.randomUUID();
    Company company = new Company("Acme", "Tech");
    Lead created = new Lead(id, "s@s.com", company, LeadStatus.NEW, LocalDateTime.now());
    when(leadService.addLead(eq("s@s.com"), any(Company.class), eq(LeadStatus.NEW)))
        .thenReturn(created);

    mockMvc
        .perform(
            post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "s@s.com",
                      "companyName": "Acme",
                      "industry": "Tech",
                      "status": "NEW"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.LOCATION, "/api/leads/" + id));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.deleteLead(id)).thenReturn(true);

    mockMvc
        .perform(delete("/api/leads/" + id))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.deleteLead(id)).thenReturn(false);

    mockMvc.perform(delete("/api/leads/" + id)).andExpect(status().isNotFound());
  }
}
