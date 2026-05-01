package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@Transactional
class LeadServiceGateThreeTest {

  @Autowired private LeadService service;

  @Autowired private LeadRepository repository;

  @Autowired private CompanyRepository companyRepository;

  // Given:
  @BeforeEach
  void setUp() {
    repository.deleteAll();
    companyRepository.deleteAll();

    for (int i = 1; i <= 3; i++) {
      Company company = new Company("Company " + i, "Industry " + i);
      companyRepository.save(company);
      Lead lead = new Lead("lead" + i + "@example.com", company, LeadStatus.NEW);
      repository.save(lead);
    }
  }

  @Test
  void shouldFindLeadsByCompanyNameAndEmail() {
    // When:
    List<Lead> found =
        service.findByStatusAndEmailAndCompanyDynamic(
            null, "lead" + 1 + "@example.com", "Company " + 1);
    // Then:
    assertThat(found).hasSize(1);
    assertThat(found.get(0).getEmail()).isEqualTo("lead" + 1 + "@example.com");
  }
}
