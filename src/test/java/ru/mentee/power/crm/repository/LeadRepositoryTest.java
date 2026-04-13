package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

  @Autowired
  private LeadRepository repository;

  // LeadRepositoryTest.java
  @Autowired
  private CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    // Создаём компании и сохраняем их
    Company company1 = new Company("ACME Corp", "Acme Industries");
    Company company2 = new Company("Tech Inc", "Tech Industries");
    companyRepository.save(company1);
    companyRepository.save(company2);

    Lead lead1 = new Lead("john@example.com", company1, LeadStatus.NEW);
    lead1.setCreatedAt(LocalDateTime.now().minusDays(5));
    repository.save(lead1);

    Lead lead2 = new Lead("jane@example.com", company2, LeadStatus.CONTACTED);
    lead2.setCreatedAt(LocalDateTime.now().minusDays(2));
    repository.save(lead2);
  }

  @Test
  void shouldSaveAndFindLeadById_whenValidData() {
    // Given
    Company company = new Company("ACME", "Acme Industries");
    companyRepository.save(company);
    Lead lead = new Lead(null, "test@example.com",
            company,
            LeadStatus.NEW, LocalDateTime.now());

    // When
    Lead saved = repository.save(lead);
    Optional<Lead> found = repository.findById(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldFindByEmailNative_whenLeadExists() {
    // Given
    Company company = new Company("ACME", "Acme Industries");
    companyRepository.save(company);
    Lead lead = new Lead(null, "test1@example.com",
            company,
            LeadStatus.NEW, LocalDateTime.now());
    repository.save(lead);

    // When
    Optional<Lead> found = repository.findByEmail("test1@example.com");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getCompany().getName()).isEqualTo("ACME");
  }

  @Test
  void shouldReturnEmptyOptional_whenEmailNotFound() {
    // When
    Optional<Lead> found = repository.findByEmail("nonexistent@test.com");

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAll() {
    // Given
    Company company = new Company("ACME", "Acme Industries");
    companyRepository.save(company);
    Lead lead = new Lead(null, "test@example.com",
            company,
            LeadStatus.NEW, LocalDateTime.now());
    repository.save(lead);
    // When
    List<Lead> found = repository.findAll();
    // Then
    assertThat(found).isNotEmpty();
  }

  @Test
  void findByEmail_shouldReturnLead_whenExists() {
    // When
    Optional<Lead> found = repository.findByEmail("john@example.com");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getCompany().getName()).isEqualTo("ACME Corp");
  }

  @Test
  void findByStatus_shouldReturnFilteredLeads() {
    // When
    List<Lead> newLeads = repository.findByStatus(LeadStatus.NEW);

    // Then
    assertThat(newLeads).hasSize(1);
    assertThat(newLeads.getFirst().getEmail()).isEqualTo("john@example.com");
  }

  @Test
  void findByStatusIn_shouldReturnLeadsWithMultipleStatuses() {
    // Given
    List<LeadStatus> statuses = List.of(LeadStatus.NEW, LeadStatus.CONTACTED);

    // When
    List<Lead> found = repository.findByStatusIn(statuses);

    // Then
    assertThat(found).hasSize(2);
  }

  @Test
  void findAll_withPageable_shouldReturnPage() {
    // Given
    PageRequest pageRequest = PageRequest.of(0, 1);

    // When
    Page<Lead> page = repository.findAll(pageRequest);

    // Then
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getNumber()).isZero(); // текущая страница
  }

  @Test
  void countByStatus_shouldReturnCount() {
    // When
    long count = repository.countByStatus(LeadStatus.NEW);
    // Then
    assertThat(count).isEqualTo(1);
    assertThat(repository.countByStatus(LeadStatus.CONTACTED)).isEqualTo(1);
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenEmailExists() {
    // when:
    boolean result = repository.existsByEmail("john@example.com");
    // then:
    assertThat(result).isTrue();
  }

}