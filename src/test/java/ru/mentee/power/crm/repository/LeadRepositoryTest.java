package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
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

  @Test
  void shouldSaveAndFindLeadById_whenValidData() {
    // Given
    Lead lead = new Lead(null, "test@example.com", "ACME",
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
    Lead lead = new Lead(null, "test@example.com", "ACME",
            LeadStatus.NEW, LocalDateTime.now());
    repository.save(lead);

    // When
    Optional<Lead> found = repository.findByEmailNative("test@example.com");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getCompany()).isEqualTo("ACME");
  }

  @Test
  void shouldReturnEmptyOptional_whenEmailNotFound() {
    // When
    Optional<Lead> found = repository.findByEmailNative("nonexistent@test.com");

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAll() {
    // Given
    Lead lead = new Lead(null, "test@example.com", "ACME",
            LeadStatus.NEW, LocalDateTime.now());
    repository.save(lead);
    // When
    List<Lead> found = repository.findAll();
    // Then
    assertThat(found).isNotEmpty();
  }

  @Test
  void shouldDelete() {
    // Given
    Lead lead = new Lead(null, "test@example.com", "ACME",
            LeadStatus.NEW, LocalDateTime.now());
    repository.save(lead);
    // When
    repository.delete(lead);
    // Then
    assertThat(repository.count()).isZero();
  }
}