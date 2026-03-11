package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.service.LeadService;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class LeadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LeadService leadService;

  @Test
  void shouldReturnLeadsPageSuccessfully() throws Exception {
    // Given
    // When
    mockMvc.perform(get("/leads"))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("leads/list"))
            .andExpect(model().attributeExists("leads"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Email")));
  }

  @Test
  void shouldReturnLeadsNewPageSuccessfully() throws Exception {
    // Given
    // When
    mockMvc.perform(get("/leads/new"))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("leads/create"));
  }

  @Test
  void shouldDeleteLeadAndRedirect() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(post("/leads/{id}/delete", id))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/leads"));

    verify(leadService).delete(id);
  }
}