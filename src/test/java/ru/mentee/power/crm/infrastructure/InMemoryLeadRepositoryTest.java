package ru.mentee.power.crm.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class InMemoryLeadRepositoryTest {

  private InMemoryLeadRepository repository;
  private Lead lead1;
  private Lead lead2;
  private Lead lead3;

  @BeforeEach
  void setUp() {
    repository = new InMemoryLeadRepository();

    // Создаём тестовые лиды
    Address address = new Address("Moscow", "Tverskaya", "123456");
    Contact contact1 = new Contact("ivan@mail.ru", "79991234567", address);
    Contact contact2 = new Contact("anna@gmail.com", "79997654321", address);
    Contact contact3 = new Contact("petr@yandex.ru", "79998887766", address);

    lead1 = new Lead(UUID.randomUUID(), contact1, "Company A", "NEW");
    lead2 = new Lead(UUID.randomUUID(), contact2, "Company B", "QUALIFIED");
    lead3 = new Lead(UUID.randomUUID(), contact3, "Company C", "NEW");
  }

  @Test
  void testAdd_WhenLeadIsNull_ShouldThrowException() {
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(null)
    );

    assertEquals("Lead cannot be null", exception.getMessage());
  }

  @Test
  void testAdd_WhenLeadIsValid_ShouldAddToStorage() {
    // Act
    repository.add(lead1);

    // Assert
    List<Lead> allLeads = repository.findAll();
    assertEquals(1, allLeads.size());
    assertTrue(allLeads.contains(lead1));
  }

  @Test
  void testAdd_WhenLeadAlreadyExists_ShouldNotAddDuplicate() {
    // Arrange
    repository.add(lead1);

    // Act - пытаемся добавить тот же лид снова
    repository.add(lead1);

    // Assert - должен быть только один элемент
    List<Lead> allLeads = repository.findAll();
    assertEquals(1, allLeads.size(), "Дубликат не должен добавляться");
  }

  @Test
  void testAdd_MultipleLeads_ShouldAllBeAdded() {
    // Act
    repository.add(lead1);
    repository.add(lead2);
    repository.add(lead3);

    // Assert
    List<Lead> allLeads = repository.findAll();
    assertEquals(3, allLeads.size());
    assertTrue(allLeads.contains(lead1));
    assertTrue(allLeads.contains(lead2));
    assertTrue(allLeads.contains(lead3));
  }

  @Test
  void testRemove_WhenIdIsNull_ShouldThrowException() {
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.remove(null)
    );

    assertEquals("id cannot be null", exception.getMessage());
  }

  @Test
  void testRemove_WhenLeadExists_ShouldRemoveFromStorage() {
    // Arrange
    repository.add(lead1);
    repository.add(lead2);
    repository.add(lead3);

    // Act
    repository.remove(lead2.id());

    // Assert
    List<Lead> allLeads = repository.findAll();
    assertEquals(2, allLeads.size());
    assertTrue(allLeads.contains(lead1));
    assertFalse(allLeads.contains(lead2));
    assertTrue(allLeads.contains(lead3));
  }

  @Test
  void testRemove_WhenLeadDoesNotExist_ShouldDoNothing() {
    // Arrange
    repository.add(lead1);
    UUID nonExistentId = UUID.randomUUID();

    // Act
    repository.remove(nonExistentId);

    // Assert
    List<Lead> allLeads = repository.findAll();
    assertEquals(1, allLeads.size());
    assertTrue(allLeads.contains(lead1));
  }

  @Test
  void testRemove_WhenStorageIsEmpty_ShouldDoNothing() {
    // Arrange
    UUID someId = UUID.randomUUID();

    // Act (не должно быть исключения)
    repository.remove(someId);

    // Assert
    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void testFindById_WhenIdIsNull_ShouldThrowException() {
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.findById(null)
    );

    assertEquals("id cannot be null", exception.getMessage());
  }

  @Test
  void testFindById_WhenLeadExists_ShouldReturnLead() {
    // Arrange
    repository.add(lead1);
    repository.add(lead2);

    // Act
    Optional<Lead> foundLead = repository.findById(lead1.id());

    // Assert
    assertTrue(foundLead.isPresent());
    assertEquals(lead1, foundLead.get());
    assertEquals(lead1.id(), foundLead.get().id());
    assertEquals(lead1.company(), foundLead.get().company());
  }

  @Test
  void testFindById_WhenLeadDoesNotExist_ShouldReturnEmptyOptional() {
    // Arrange
    repository.add(lead1);
    UUID nonExistentId = UUID.randomUUID();

    // Act
    Optional<Lead> foundLead = repository.findById(nonExistentId);

    // Assert
    assertFalse(foundLead.isPresent());
  }

  @Test
  void testFindById_WhenStorageIsEmpty_ShouldReturnEmptyOptional() {
    // Arrange
    UUID someId = UUID.randomUUID();

    // Act
    Optional<Lead> foundLead = repository.findById(someId);

    // Assert
    assertFalse(foundLead.isPresent());
  }

  @Test
  void testFindAll_WhenStorageIsEmpty_ShouldReturnEmptyList() {
    // Act
    List<Lead> allLeads = repository.findAll();

    // Assert
    assertNotNull(allLeads);
    assertTrue(allLeads.isEmpty());
  }

  @Test
  void testFindAll_ReturnsDefensiveCopy_OriginalStorageNotAffected() {
    // Arrange
    repository.add(lead1);
    repository.add(lead2);

    // Act
    List<Lead> allLeads = repository.findAll();

    // Изменяем возвращённый список (не должен влиять на storage)
    allLeads.clear();
    allLeads.add(lead3);  // добавляем новый лид в копию

    // Assert - оригинальный storage не изменился
    List<Lead> originalLeads = repository.findAll();
    assertEquals(2, originalLeads.size());
    assertTrue(originalLeads.contains(lead1));
    assertTrue(originalLeads.contains(lead2));
    assertFalse(originalLeads.contains(lead3));
  }

  @Test
  void testIntegration_AddRemoveFindOperations() {
    // Тест на последовательность операций

    // 1. Добавляем лиды
    repository.add(lead1);
    repository.add(lead2);

    assertEquals(2, repository.findAll().size());

    // 2. Находим лид по ID
    Optional<Lead> found = repository.findById(lead1.id());
    assertTrue(found.isPresent());
    assertEquals(lead1.company(), found.get().company());

    // 3. Удаляем один лид
    repository.remove(lead1.id());

    // 4. Проверяем, что остался только второй лид
    List<Lead> remaining = repository.findAll();
    assertEquals(1, remaining.size());
    assertEquals(lead2, remaining.get(0));

    // 5. Пытаемся найти удалённый лид
    Optional<Lead> deleted = repository.findById(lead1.id());
    assertFalse(deleted.isPresent());

    // 6. Добавляем третий лид
    repository.add(lead3);

    // 7. Проверяем финальное состояние
    List<Lead> finalList = repository.findAll();
    assertEquals(2, finalList.size());
    assertTrue(finalList.contains(lead2));
    assertTrue(finalList.contains(lead3));
  }
/*
  @Test
  void testEqualsAndHashCode_ForDuplicateDetection() {
    // Этот тест проверяет, что contains() работает правильно
    // Для этого у Lead должен быть правильно реализован equals/hashCode

    // Arrange - два лида с одинаковым ID (должны считаться одинаковыми)
    UUID sameId = UUID.randomUUID();
    Address address = new Address("Moscow", "Tverskaya", "123456");
    Contact contact1 = new Contact("test1@mail.ru", "79991111111", address);
    Contact contact2 = new Contact("test2@mail.ru", "79992222222", address);

    Lead leadA = new Lead(sameId, contact1, "Company A", "NEW");
    Lead leadB = new Lead(sameId, contact2, "Company B", "QUALIFIED");

    // Act
    repository.add(leadA);
    repository.add(leadB);  // Этот вызов должен НЕ добавить лид, так как ID совпадает

    // Assert
    assertEquals(1, repository.findAll().size());
    assertEquals(leadA, repository.findById(sameId).orElse(null));
  }
  */
}