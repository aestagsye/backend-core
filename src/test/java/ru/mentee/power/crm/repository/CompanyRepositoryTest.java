package ru.mentee.power.crm.repository;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CompanyRepositoryTest {

  @Autowired
  private CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    companyRepository.deleteAll();
  }

  @Test
  void shouldFindById() {
    Company company = companyRepository.save(new Company("Test Company", "Tech"));

    Company found = companyRepository.findById(company.getId()).orElseThrow();

    assertThat(found.getName()).isEqualTo("Test Company");
  }

  @Test
  void shouldReturnEmptyForNonExistentId() {
    var found = companyRepository.findById(java.util.UUID.randomUUID());

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByName() {
    companyRepository.save(new Company("Acme Corp", "Tech"));

    Optional<Company> found = companyRepository.findByName("Acme Corp");

    assertThat(found).isPresent();
    assertThat(found.get().getIndustry()).isEqualTo("Tech");
  }

  @Test
  void shouldReturnEmptyWhenNameNotFound() {
    companyRepository.save(new Company("Acme Corp", "Tech"));

    Optional<Company> found = companyRepository.findByName("NonExistent");

    assertThat(found).isEmpty();
  }

  @Test
  void shouldSaveAndFindCompany() {
    Company company = new Company("Test Company", "Retail");

    Company saved = companyRepository.save(company);

    Optional<Company> found = companyRepository.findById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Test Company");
  }

  @Test
  void shouldDeleteCompany() {
    Company company = companyRepository.save(new Company("Delete Me", "Tech"));

    companyRepository.delete(company);

    assertThat(companyRepository.findById(company.getId())).isEmpty();
  }

  @Test
  void shouldFindAllCompanies() {
    companyRepository.save(new Company("Company A", "Tech"));
    companyRepository.save(new Company("Company B", "Retail"));

    var all = companyRepository.findAll();

    assertThat(all).hasSize(2);
  }
}
