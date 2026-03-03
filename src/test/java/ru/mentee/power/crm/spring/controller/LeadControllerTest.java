package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LeadControllerTest {

  @Autowired
  private MockMvc mockMvc;

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

}