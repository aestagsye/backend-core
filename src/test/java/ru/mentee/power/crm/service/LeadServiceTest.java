package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@Transactional
class LeadServiceTest {

  @Autowired private LeadService service;

  @Autowired private LeadRepository repository;

  @Autowired private CompanyRepository companyRepository;

  // Given
  @BeforeEach
  void setUp() {
    repository.deleteAll();
    companyRepository.deleteAll();

    // Создаём 3 NEW лида
    for (int i = 1; i <= 3; i++) {
      Company company = new Company("Company " + i, "Industry " + i);
      companyRepository.save(company);
      Lead lead = new Lead("lead" + i + "@example.com", company, LeadStatus.NEW);
      repository.save(lead);
    }
  }

  @Test
  void shouldCreateLead_whenEmailIsUnique() {
    // When
    Lead result = repository.findByEmail("lead" + 1 + "@example.com").get();
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("lead" + 1 + "@example.com");
    assertThat(result.getCompany().getName()).isEqualTo("Company " + 1);
    assertThat(result.getStatus()).isEqualTo(LeadStatus.NEW);
    assertThat(result.getId()).isNotNull();
  }

  @Test
  void shouldThrowException_whenEmailAlreadyExists() {
    // Given
    String email = "duplicate@example.com";
    service.addLead(email, new Company("First Company", "First Industry"), LeadStatus.NEW);

    // When/Then
    assertThatThrownBy(
            () ->
                service.addLead(
                    email, new Company("Second Company", "Second Industry"), LeadStatus.NEW))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Lead with email already exists");
  }

  @Test
  void shouldFindAllLeads() {
    // When
    List<Lead> result = service.findAll();

    // Then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldFindLeadById() {
    // Given
    Lead created =
        service.addLead("find@example.com", new Company("Company", "Industry"), LeadStatus.NEW);

    // When
    Optional<Lead> result = service.findById(created.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindLeadByEmail() {
    // Given
    service.addLead("search@example.com", new Company("Company", "Industry"), LeadStatus.NEW);

    // When
    Optional<Lead> result = service.findByEmail("search@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getCompany().getName()).isEqualTo("Company");
  }

  @Test
  void shouldReturnEmpty_whenLeadNotFound() {
    // Given/When
    Optional<Lead> result = service.findByEmail("nonexistent@example.com");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnOnlyExactLeads_whenFindByExactStatus() {
    // When
    List<Lead> result = service.findByStatus(LeadStatus.NEW);
    // Then
    assertThat(result).hasSize(3).allMatch(lead -> lead.getStatus().equals(LeadStatus.NEW));
  }

  @Test
  void shouldReturnEmptyList_whenNoLeadsWithStatus() {
    // Given
    for (int i = 0; i < 8; i++) {
      if (i < 3) {
        service.addLead(
            "new" + i + "@n.com", new Company("EvilCorp" + i, "Industry"), LeadStatus.NEW);
      } else {
        service.addLead(
            "contacted" + i + "@c.com",
            new Company("NeutralCorp" + i, "Industry" + i),
            LeadStatus.CONTACTED);
      }
    }
    // When
    List<Lead> result = service.findByStatus(LeadStatus.QUALIFIED);
    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldUpdateLead() {
    // given
    Lead created =
        service.addLead("dorzh@mail.ru", new Company("AcmeCorp", "Acme Industry"), LeadStatus.NEW);
    UUID id = created.getId();

    // when
    Lead existing = service.findById(id).orElseThrow();
    existing.setEmail("bebra@b.com");
    existing.getCompany().setName("EvilCorp");
    existing.setStatus(LeadStatus.QUALIFIED);
    service.update(id, existing);

    // then
    assertThat(service.findByEmail("bebra@b.com")).contains(existing);
  }

  @Test
  void shouldThrowException_whenUpdateWithNonExistingId() {
    UUID uuid = UUID.randomUUID();
    Lead lead =
        new Lead(
            uuid,
            "borsh@b.com",
            new Company("AcmeCorp", "Acme Industry"),
            LeadStatus.NEW,
            LocalDateTime.now());
    assertThatThrownBy(() -> service.update(uuid, lead))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("There is no Lead with such id");
  }

  @Test
  void shouldDeleteLead() {
    service.addLead("dorzh@mail.ru", new Company("AcmeCorp", "Acme Industry"), LeadStatus.NEW);
    UUID uuid = null;
    if (service.findByEmail("dorzh@mail.ru").isPresent()) {
      uuid = service.findByEmail("dorzh@mail.ru").get().getId();
    }
    service.delete(uuid);
    assertThat(service.findById(uuid)).isEmpty();
  }

  @Test
  void shouldThrowException_whenDeleteNonExistentLead() {
    UUID uuid = UUID.randomUUID();
    assertThatThrownBy(() -> service.delete(uuid))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldReturnAllLeads_whenNoFilters() {
    // When
    List<Lead> result = service.findLeads(null, null);

    // Then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldFilterByStatus() {
    // When
    List<Lead> resultNew = service.findLeads(null, LeadStatus.NEW);
    List<Lead> resultContacted = service.findLeads(null, LeadStatus.CONTACTED);
    List<Lead> resultQualified = service.findLeads(null, LeadStatus.QUALIFIED);

    // Then
    assertThat(resultNew).hasSize(3);
    assertThat(resultContacted).hasSize(0);
    assertThat(resultQualified).hasSize(0);
  }

  @Test
  void shouldReturnEmpty_whenNoMatch() {
    // Given
    service.addLead("a@b.com", new Company("CompanyA", "A Industry"), LeadStatus.NEW);

    // When
    List<Lead> resultNonexistent = service.findLeads("nonexistent", null);
    List<Lead> resultWrongStatus = service.findLeads(null, LeadStatus.QUALIFIED);
    List<Lead> resultCombined = service.findLeads("a@b.com", LeadStatus.QUALIFIED);

    // Then
    assertThat(resultNonexistent).isEmpty();
    assertThat(resultWrongStatus).isEmpty();
    assertThat(resultCombined).isEmpty();
  }

  @Test
  void shouldIgnoreBlankSearch() {
    // When
    List<Lead> resultEmpty = service.findLeads("", null);
    List<Lead> resultBlank = service.findLeads("   ", null);

    // Then
    assertThat(resultEmpty).hasSize(3);
    assertThat(resultBlank).hasSize(3);
  }

  @Test
  void convertNewToContacted_shouldUpdateMultipleLeads() {
    // When
    int updated = service.convertNewToContacted();

    // Then
    assertThat(updated).isEqualTo(3);

    // Проверяем что статус изменился
    long contactedCount = repository.countByStatus(LeadStatus.CONTACTED);
    assertThat(contactedCount).isEqualTo(3);

    long newCount = repository.countByStatus(LeadStatus.NEW);
    assertThat(newCount).isEqualTo(0);
  }

  @Test
  void archiveOldLeads() {
    // When
    int deleted = service.archiveOldLeads(LeadStatus.NEW);

    // Then
    assertThat(deleted).isEqualTo(3);
  }
}
