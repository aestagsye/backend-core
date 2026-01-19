package ru.mentee.power.crm.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class LeadRepositoryTest {
  private LeadRepository repository;
  private Contact contact1;
  private Lead lead1;
  private Lead lead2;

  @BeforeEach
  void setUp() {
    repository = new LeadRepository();

    Address address = new Address("Moscow", "Tverskaya", "123456");
    contact1 = new Contact("ivan@mail.ru", "79991234567", address);

    lead1 = new Lead(UUID.randomUUID(), contact1, "Company A", "NEW");
    lead2 = new Lead(UUID.randomUUID(), new Contact("anna@gmail.com", "79997654321",
            new Address("Mos", "Lenina", "223228")), "Company B", "QUALIFIED");
  }

  @Test
  @DisplayName("Should automatically deduplicate leads by id")
  void shouldDeduplicateLeadsById() {
    // Given: done in setUp()
    // When:
    repository.add(lead1);
    boolean result = repository.add(lead1);
    // Then:
    assertThat(repository.size()).isEqualTo(1);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should allow different leads with different ids")
  void shouldAllowDifferentLeads() {
    // Given: done in setUp()
    // When:
    boolean result1 = repository.add(lead1);
    boolean result2 = repository.add(lead2);
    // Then:
    assertThat(repository.size()).isEqualTo(2);
    assertThat(result1).isTrue();
    assertThat(result2).isTrue();
  }

  @Test
  @DisplayName("Should find existing lead through contains")
  void shouldFindExistingLead() {
    // Given:
    repository.add(lead1);
    // When:
    boolean result = repository.contains(lead1);
    // Then:
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Should return unmodifiable set from findAll")
  void shouldReturnUnmodifiableSet() {
    // Given:
    repository.add(lead1);
    // When:
    Set<Lead> leads = repository.findAll();
    // Then:
    assertThatThrownBy(() -> leads.add(lead2)).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("Should perform contains() faster than ArrayList")
  void shouldPerformFasterThanArrayList() {
    // Given:
    List<Lead> leads = new ArrayList<>(10000);
    for (int i = 1; i <= 10000; i++) {
      Lead lead3 = new Lead(UUID.randomUUID(), contact1, "bibi", "NEW");
      leads.add(lead3);
      repository.add(lead3);
    }
    Lead searchLead = leads.get(5000);
    // When:
    long hashSetStart = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      repository.contains(searchLead);
    }
    long hashSetDuration = System.nanoTime() - hashSetStart;

    long listStart = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      leads.contains(searchLead);
    }
    long listDuration = System.nanoTime() - listStart;
    // Then:
    double ratio = (double) listDuration/hashSetDuration;
    assertThat(ratio).isGreaterThanOrEqualTo(100.0);
  }

  @Test
  void shouldSaveBothLeads_evenWithSameEmailAndPhone_becauseRepositoryDoesNotCheckBusinessRules() {
    // Given: два лида с разными UUID но одинаковыми контактами
    Contact sharedContact = new Contact("ivan@mail.ru", "+79001234567",
            new Address("Moscow", "Tverskaya 1", "101000"));
    Lead originalLead = new Lead(UUID.randomUUID(), sharedContact, "Acme Corp", "NEW");
    Lead duplicateLead = new Lead(UUID.randomUUID(), sharedContact, "TechCorp", "NEW");

    // When: сохраняем оба
    repository.add(originalLead);
    repository.add(duplicateLead);

    // Then: Repository сохранил оба (это технически правильно!)
    assertThat(repository.size()).isEqualTo(2);

    // But: Бизнес недоволен — в CRM два контакта на одного человека
    // Решение: Service Layer в Sprint 5 будет проверять бизнес-правила
    // перед вызовом repository.save()
  }
}