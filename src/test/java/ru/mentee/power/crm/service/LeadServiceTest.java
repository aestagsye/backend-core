package ru.mentee.power.crm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getCompany()).isEqualTo(company);
    assertThat(result.getStatus()).isEqualTo(status);
    assertThat(result.getId()).isNotNull();
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
    Optional<Lead> result = service.findById(created.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindLeadByEmail() {
    // Given
    service.addLead("search@example.com", "Company", LeadStatus.NEW);

    // When
    Optional<Lead> result = service.findByEmail("search@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getCompany()).isEqualTo("Company");
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
            .allMatch(lead -> lead.getStatus().equals(LeadStatus.NEW));
    assertThat(result1)
            .hasSize(5)
            .allMatch(lead -> lead.getStatus().equals(LeadStatus.CONTACTED));
    assertThat(result2)
            .hasSize(2)
            .allMatch(lead -> lead.getStatus().equals(LeadStatus.QUALIFIED));
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

  @Test
  void shouldUpdateLead() {
    service.addLead("dorzh@mail.ru","AcmeCorp",LeadStatus.NEW);
    UUID uuid = null;
    if (service.findByEmail("dorzh@mail.ru").isPresent()) {
      uuid = service.findByEmail("dorzh@mail.ru").get().getId();
    }
    Lead updatedLead = new Lead(uuid, "bebra@b.com","EvilCorp",
            LeadStatus.QUALIFIED, LocalDateTime.now());
    service.update(uuid, updatedLead);
    assertThat(service.findByEmail("bebra@b.com")).contains(updatedLead);
  }

  @Test
  void shouldThrowException_whenUpdateWithNonExistingId() {
    UUID uuid = UUID.randomUUID();
    Lead lead = new Lead(uuid,"borsh@b.com","acme",LeadStatus.NEW, LocalDateTime.now());
    assertThatThrownBy( () ->
            service.update(uuid,lead)
    )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("There is no Lead with such id");
  }

  @Test
  void shouldDeleteLead() {
    service.addLead("dorzh@mail.ru","AcmeCorp",LeadStatus.NEW);
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
    assertThatThrownBy( () ->
            service.delete(uuid)
    )
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldReturnAllLeads_whenNoFilters() {
    // Given
    service.addLead("a@b.com", "CompanyA", LeadStatus.NEW);
    service.addLead("c@d.com", "CompanyB", LeadStatus.CONTACTED);
    service.addLead("e@f.com", "CompanyC", LeadStatus.QUALIFIED);

    // When
    List<Lead> result = service.findLeads(null, null);

    // Then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldFilterBySearchTerm() {
    // Given
    service.addLead("john@example.com", "John's Company", LeadStatus.NEW);
    service.addLead("jane@example.com", "Jane's Company", LeadStatus.CONTACTED);
    service.addLead("bob@example.com", "Bob's Company", LeadStatus.QUALIFIED);

    // When
    List<Lead> resultByEmail = service.findLeads("john", null);
    List<Lead> resultByCompany = service.findLeads("Jane's", null);
    List<Lead> resultCaseInsensitive = service.findLeads("JOHN", null);
    List<Lead> resultMultiple = service.findLeads("example.com", null);

    // Then
    assertThat(resultByEmail).hasSize(1).allMatch(lead -> lead.getEmail().contains("john"));
    assertThat(resultByCompany).hasSize(1).allMatch(lead -> lead.getCompany().contains("Jane's"));
    assertThat(resultCaseInsensitive).hasSize(1).allMatch(lead -> lead.getEmail().equalsIgnoreCase("john@example.com"));
    assertThat(resultMultiple).hasSize(3);
  }

  @Test
  void shouldFilterByStatus() {
    // Given
    service.addLead("a@b.com", "CompanyA", LeadStatus.NEW);
    service.addLead("c@d.com", "CompanyB", LeadStatus.CONTACTED);
    service.addLead("e@f.com", "CompanyC", LeadStatus.CONTACTED);
    service.addLead("g@h.com", "CompanyD", LeadStatus.QUALIFIED);

    // When
    List<Lead> resultNew = service.findLeads(null, LeadStatus.NEW);
    List<Lead> resultContacted = service.findLeads(null, LeadStatus.CONTACTED);
    List<Lead> resultQualified = service.findLeads(null, LeadStatus.QUALIFIED);

    // Then
    assertThat(resultNew).hasSize(1);
    assertThat(resultContacted).hasSize(2);
    assertThat(resultQualified).hasSize(1);
  }

  @Test
  void shouldFilterBySearchAndStatus() {
    // Given
    service.addLead("alice@company.com", "Alice Inc", LeadStatus.NEW);
    service.addLead("bob@company.com", "Bob Ltd", LeadStatus.CONTACTED);
    service.addLead("carol@company.com", "Carol LLC", LeadStatus.NEW);
    service.addLead("dave@company.com", "Dave Corp", LeadStatus.CONTACTED);

    // When
    List<Lead> result = service.findLeads("alice", LeadStatus.NEW);
    List<Lead> result2 = service.findLeads("company", LeadStatus.CONTACTED);

    // Then
    assertThat(result).hasSize(1)
            .allMatch(lead -> lead.getEmail().contains("alice") && lead.getStatus() == LeadStatus.NEW);
    assertThat(result2).hasSize(2)
            .allMatch(lead -> lead.getStatus() == LeadStatus.CONTACTED);
  }

  @Test
  void shouldReturnEmpty_whenNoMatch() {
    // Given
    service.addLead("a@b.com", "CompanyA", LeadStatus.NEW);

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
    // Given
    service.addLead("a@b.com", "CompanyA", LeadStatus.NEW);
    service.addLead("c@d.com", "CompanyB", LeadStatus.CONTACTED);

    // When
    List<Lead> resultEmpty = service.findLeads("", null);
    List<Lead> resultBlank = service.findLeads("   ", null);

    // Then
    assertThat(resultEmpty).hasSize(2);
    assertThat(resultBlank).hasSize(2);
  }
}