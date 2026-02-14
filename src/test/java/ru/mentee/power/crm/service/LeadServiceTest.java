package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;

class LeadServiceTest {

  private LeadService service;

  @BeforeEach
  void setUp() {
    LeadRepository repository = new InMemoryLeadRepository();
    service = new LeadService(repository);
  }

  @Test
  void shouldCreateLead_whenEmailIsUnique() {
    // Given
    String email = "test@example.com";
    String company = "Test Company";
    LeadStatus status = LeadStatus.NEW;

    // When
    Lead result = service.addLead(email, company, status);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo(email);
    assertThat(result.company()).isEqualTo(company);
    assertThat(result.status()).isEqualTo(status);
    assertThat(result.id()).isNotNull();
  }

  @Test
  void shouldThrowException_whenEmailAlreadyExists() {
    // Given
    String email = "duplicate@example.com";
    service.addLead(email, "First Company", LeadStatus.NEW);

    // When/Then
    assertThatThrownBy(() ->
            service.addLead(email, "Second Company", LeadStatus.NEW)
    )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Lead with email already exists");
  }

  @Test
  void shouldFindAllLeads() {
    // Given
    service.addLead("one@example.com", "Company 1", LeadStatus.NEW);
    service.addLead("two@example.com", "Company 2", LeadStatus.CONTACTED);

    // When
    List<Lead> result = service.findAll();

    // Then
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldFindLeadById() {
    // Given
    Lead created = service.addLead("find@example.com", "Company", LeadStatus.NEW);

    // When
    Optional<Lead> result = service.findById(created.id());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().email()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindLeadByEmail() {
    // Given
    service.addLead("search@example.com", "Company", LeadStatus.NEW);

    // When
    Optional<Lead> result = service.findByEmail("search@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().company()).isEqualTo("Company");
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
    // Given
    for (int i = 0; i < 10; i++) {
      if (i<3) {
        service.addLead("new"+i+"@n.com","EvilCorp"+i, LeadStatus.NEW);
      } else if (i<8) {
        service.addLead("contacted"+i+"@c.com", "NeutralCorp"+i, LeadStatus.CONTACTED);
      } else {
        service.addLead("qualified"+i+"@q.com","AngelCorp"+i,LeadStatus.QUALIFIED);
      }
    }
    // When
    List<Lead> result = service.findByStatus(LeadStatus.NEW);
    List<Lead> result1 = service.findByStatus(LeadStatus.CONTACTED);
    List<Lead> result2 = service.findByStatus(LeadStatus.QUALIFIED);
    // Then
    assertThat(result)
            .hasSize(3)
            .allMatch(lead -> lead.status().equals(LeadStatus.NEW));
    assertThat(result1)
            .hasSize(5)
            .allMatch(lead -> lead.status().equals(LeadStatus.CONTACTED));
    assertThat(result2)
            .hasSize(2)
            .allMatch(lead -> lead.status().equals(LeadStatus.QUALIFIED));
  }

  @Test
  void shouldReturnEmptyList_whenNoLeadsWithStatus() {
    // Given
    for (int i = 0; i < 8; i++) {
      if (i<3) {
        service.addLead("new"+i+"@n.com","EvilCorp"+i, LeadStatus.NEW);
      } else {
        service.addLead("contacted"+i+"@c.com", "NeutralCorp"+i, LeadStatus.CONTACTED);
      }
    }
    // When
    List<Lead> result = service.findByStatus(LeadStatus.QUALIFIED);
    // Then
    assertThat(result).isEmpty();
  }
}