package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;

class LeadRepositoryTest {
  private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository = new LeadRepository();
  }

  @Test
  void shouldSaveAndFindLeadById_whenLeadSaved() {
    // given:
    Lead lead = new Lead("1","t@t.com", "+12",
            "Lotos", "NEW");
    // when:
    repository.save(lead);
    // then:
    Lead lead1 = repository.findById("1");
    assertThat(lead1).isNotNull();
  }

  @Test
  void shouldReturnNull_whenLeadNotFound() {
    //given:

    //when:
    Lead lead = repository.findById("unknown-id");
    //then:
    assertThat(lead).isNull();
  }

  @Test
  void shouldReturnAllLeads_whenMultipleLeadsSaved() {
    //given
    repository.save(new Lead("1","t@t.com", "+12",
            "Lotos", "NEW"));
    repository.save(new Lead("2","t@t.com", "+12",
            "Lotos", "NEW"));
    repository.save(new Lead("3","t@t.com", "+12",
            "Lotos", "NEW"));
    //when:
    assertThat(repository.findAll()).hasSize(3);
  }

  @Test
  void shouldDeleteLead_whenLeadExists() {
    //given:
    repository.save(new Lead("1","a@a.com","+1",
            "Apple","noob"));
    //when:
    repository.delete("1");
    //then:
    assertThat(repository.findById("1")).isNull();
    assertThat(repository.size()).isZero();
  }

  @Test
  void shouldOverwriteLead_whenSaveWithSameId() {
    //given:
    repository.save(new Lead("lead-1","a@a.com","+1",
            "apple","noob"));
    //when:
    Lead lead = new Lead("lead-1","s@s.com","+2",
            "samsung","pro");
    repository.save(lead);
    //then:
    assertThat(repository.findById("lead-1")).isEqualTo(lead);
  }

  @Test
  void shouldFindFasterWithMap_thanWithListFilter() {
    // Given: Создать 1000 лидов
    List<Lead> leadList = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      Lead lead = new Lead("lead-" + i, "a" + i + "@a.com", "Company" + i,
              "tribe" + i,"lvl" + i);
      repository.save(lead);
      leadList.add(lead);
    }

    String targetId = "lead-500";  // Средний элемент

    // When: Поиск через Map
    long mapStart = System.nanoTime();
    Lead foundInMap = repository.findById(targetId);
    long mapDuration = System.nanoTime() - mapStart;

    // When: Поиск через List.stream().filter()
    long listStart = System.nanoTime();
    Lead foundInList = leadList.stream()
            .filter(lead -> lead.id().equals(targetId))
            .findFirst()
            .orElse(null);
    long listDuration = System.nanoTime() - listStart;

    // Then: Map должен быть минимум в 10 раз быстрее
    assertThat(foundInMap).isEqualTo(foundInList);
    assertThat(listDuration).isGreaterThan(mapDuration * 10);

    System.out.println("Map поиск: " + mapDuration + " ns");
    System.out.println("List поиск: " + listDuration + " ns");
    System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
  }

  @Test
  void shouldSaveBothLeads_evenWithSameEmailAndPhone_becauseRepositoryDoesNotCheckBusinessRules() {
    // Given: два лида с разными UUID но одинаковыми контактами
    Lead originalLead = new Lead("1", "a@a.com", "+2", "TechCorp","OLD");
    Lead duplicateLead = new Lead("2","a@a.com" , "+2", "AcmeCorp","NEW");

    // When: сохраняем оба
    repository.save(originalLead);
    repository.save(duplicateLead);

    // Then: Repository сохранил оба (это технически правильно!)
    assertThat(repository.size()).isEqualTo(2);

    // But: Бизнес недоволен — в CRM два контакта на одного человека
    // Решение: Service Layer в Sprint 5 будет проверять бизнес-правила
    // перед вызовом repository.save()
  }
}