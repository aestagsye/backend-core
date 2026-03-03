package ru.mentee.power.crm.spring.controller;

import ru.mentee.power.crm.spring.MockLeadService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class LeadControllerUnitTest {

  @Test
  void shouldCreateControllerWithoutSpring() {
    // Given:
    MockLeadService mockService = new MockLeadService();

    // When:
    LeadController controller = new LeadController(mockService);

    // Then:
    String response = controller.home();
    assertThat(response).contains("2 leads");
  }

  @Test
  void shouldUseInjectedService() {
    // Given
    MockLeadService mockService = new MockLeadService();
    LeadController controller = new LeadController(mockService);

    // When:
    String response = controller.home();

    // Then:
    assertThat(response).isNotNull()
            .contains("Spring Boot CRM is running");
  }
}