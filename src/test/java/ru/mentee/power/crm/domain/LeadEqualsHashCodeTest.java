package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class LeadEqualsHashCodeTest {

  @Test
  void shouldBeReflexive_whenEqualsCalledOnSameObject() {
    // Given
    Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Объект равен сам себе (isEqualTo использует equals() внутри)
    assertThat(lead).isEqualTo(lead);
  }

  @Test
  void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
    // Given
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Симметричность — порядок сравнения не важен
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(firstLead);
  }

  @Test
  void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
    // Given
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead thirdLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Транзитивность — если A=B и B=C, то A=C
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(thirdLead);
    assertThat(firstLead).isEqualTo(thirdLead);
  }

  @Test
  void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
    // Given
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Результат одинаковый при многократных вызовах
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldReturnFalse_whenEqualsComparedWithNull() {
    // Given
    Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Объект не равен null (isNotEqualTo проверяет equals(null) = false)
    assertThat(lead).isNotEqualTo(null);
  }

  @Test
  void shouldHaveSameHashCode_whenObjectsAreEqual() {
    // Given
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Если объекты равны, то hashCode должен быть одинаковым
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldWorkInHashMap_whenLeadUsedAsKey() {
    // Given
    Lead keyLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead lookupLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

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
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead differentLead = new Lead("2", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    // Then: Разные id = разные объекты (isNotEqualTo использует equals() внутри)
    assertThat(firstLead).isNotEqualTo(differentLead);
  }
}