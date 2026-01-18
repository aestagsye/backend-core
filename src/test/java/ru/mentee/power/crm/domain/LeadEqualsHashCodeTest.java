package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class LeadEqualsHashCodeTest {

  @Test
  void shouldBeReflexive_whenEqualsCalledOnSameObject() {
    // Given
    Lead lead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Объект равен сам себе (isEqualTo использует equals() внутри)
    assertThat(lead).isEqualTo(lead);
  }

  @Test
  void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
    // Given
    UUID id = UUID.randomUUID();
    Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Симметричность — порядок сравнения не важен
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(firstLead);
  }

  @Test
  void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
    // Given
    UUID id = UUID.randomUUID();
    Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead thirdLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Транзитивность — если A=B и B=C, то A=C
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(thirdLead);
    assertThat(firstLead).isEqualTo(thirdLead);
  }

  @Test
  void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
    // Given
    UUID id = UUID.randomUUID();
    Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Результат одинаковый при многократных вызовах
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldReturnFalse_whenEqualsComparedWithNull() {
    // Given
    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Объект не равен null (isNotEqualTo проверяет equals(null) = false)
    assertThat(lead).isNotEqualTo(null);
  }

  @Test
  void shouldHaveSameHashCode_whenObjectsAreEqual() {
    // Given
    UUID id = UUID.randomUUID();
    Lead firstLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Если объекты равны, то hashCode должен быть одинаковым
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldWorkInHashMap_whenLeadUsedAsKey() {
    // Given
    UUID id = UUID.randomUUID();
    Lead keyLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead lookupLead = new Lead(id, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    Map<Lead, String> map = new HashMap<>();
    map.put(keyLead, "CONTACTED");

    // When: Получаем значение по другому объекту с тем же id
    String status = map.get(lookupLead);

    // Then: HashMap нашел значение благодаря equals/hashCode
    assertThat(status).isEqualTo("CONTACTED");
  }

  @Test
  void shouldNotBeEqual_whenIdsAreDifferent() {
    // Given
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Lead firstLead = new Lead(id1, "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead differentLead = new Lead(id2, "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Разные id = разные объекты (isNotEqualTo использует equals() внутри)
    assertThat(firstLead).isNotEqualTo(differentLead);
  }
}