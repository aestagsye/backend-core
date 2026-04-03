package ru.mentee.power.crm.service;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.Application;
import ru.mentee.power.crm.repository.LeadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class LeadServiceBeanTest {
  @Autowired
  private ApplicationContext context;

  @Test
  void shouldCreateLeadServiceBean() {
    LeadService service = context.getBean(LeadService.class);
    assertThat(service).isNotNull();
  }

  @Test
  void shouldCreateLeadRepositoryBean() {
    LeadRepository repo = context.getBean(LeadRepository.class);
    assertThat(repo).isNotNull();
  }

  @Test
  void shouldInjectLeadRepositoryIntoService() {
    LeadService service = context.getBean(LeadService.class);
    assertThat(service.findAll()).isEmpty();
  }
}