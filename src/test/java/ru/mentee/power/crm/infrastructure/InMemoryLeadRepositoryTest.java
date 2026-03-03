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
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(null)
    );

    assertEquals("Lead cannot be null", exception.getMessage());
  }

  @Test
  void testAdd_WhenLeadIsValid_ShouldAddToStorage() {
    repository.add(lead1);

    List<Lead> allLeads = repository.findAll();
    assertEquals(1, allLeads.size());
    assertTrue(allLeads.contains(lead1));
  }

  @Test
  void testAdd_WhenLeadAlreadyExists_ShouldNotAddDuplicate() {
    repository.add(lead1);

    repository.add(lead1);

    List<Lead> allLeads = repository.findAll();
    assertEquals(1, allLeads.size(), "Дубликат не должен добавляться");
  }

  @Test
  void testAdd_MultipleLeads_ShouldAllBeAdded() {
    repository.add(lead1);
    repository.add(lead2);
    repository.add(lead3);

    List<Lead> allLeads = repository.findAll();
    assertEquals(3, allLeads.size());
    assertTrue(allLeads.contains(lead1));
    assertTrue(allLeads.contains(lead2));
    assertTrue(allLeads.contains(lead3));
  }

  @Test
  void testRemove_WhenIdIsNull_ShouldThrowException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.remove(null)
    );

    assertEquals("id cannot be null", exception.getMessage());
  }

  @Test
  void testRemove_WhenLeadExists_ShouldRemoveFromStorage() {
    repository.add(lead1);
    repository.add(lead2);
    repository.add(lead3);

    repository.remove(lead2.id());

    List<Lead> allLeads = repository.findAll();
    assertEquals(2, allLeads.size());
    assertTrue(allLeads.contains(lead1));
    assertFalse(allLeads.contains(lead2));
    assertTrue(allLeads.contains(lead3));
  }

  @Test
  void testRemove_WhenLeadDoesNotExist_ShouldDoNothing() {
    repository.add(lead1);
    UUID nonExistentId = UUID.randomUUID();

    repository.remove(nonExistentId);

    List<Lead> allLeads = repository.findAll();
    assertEquals(1, allLeads.size());
    assertTrue(allLeads.contains(lead1));
  }

  @Test
  void testRemove_WhenStorageIsEmpty_ShouldDoNothing() {
    UUID someId = UUID.randomUUID();

    repository.remove(someId);

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void testFindById_WhenIdIsNull_ShouldThrowException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.findById(null)
    );

    assertEquals("id cannot be null", exception.getMessage());
  }

  @Test
  void testFindById_WhenLeadExists_ShouldReturnLead() {
    repository.add(lead1);
    repository.add(lead2);

    Optional<Lead> foundLead = repository.findById(lead1.id());

    assertTrue(foundLead.isPresent());
    assertEquals(lead1, foundLead.get());
    assertEquals(lead1.id(), foundLead.get().id());
    assertEquals(lead1.company(), foundLead.get().company());
  }

  @Test
  void testFindById_WhenLeadDoesNotExist_ShouldReturnEmptyOptional() {
    repository.add(lead1);
    UUID nonExistentId = UUID.randomUUID();

    Optional<Lead> foundLead = repository.findById(nonExistentId);

    assertFalse(foundLead.isPresent());
  }

  @Test
  void testFindById_WhenStorageIsEmpty_ShouldReturnEmptyOptional() {
    UUID someId = UUID.randomUUID();

    Optional<Lead> foundLead = repository.findById(someId);

    assertFalse(foundLead.isPresent());
  }

  @Test
  void testFindAll_WhenStorageIsEmpty_ShouldReturnEmptyList() {
    List<Lead> allLeads = repository.findAll();

    assertNotNull(allLeads);
    assertTrue(allLeads.isEmpty());
  }

  @Test
  void testFindAll_ReturnsDefensiveCopy_OriginalStorageNotAffected() {
    repository.add(lead1);
    repository.add(lead2);

    List<Lead> allLeads = repository.findAll();

    allLeads.clear();
    allLeads.add(lead3);

    List<Lead> originalLeads = repository.findAll();
    assertEquals(2, originalLeads.size());
    assertTrue(originalLeads.contains(lead1));
    assertTrue(originalLeads.contains(lead2));
    assertFalse(originalLeads.contains(lead3));
  }

  @Test
  void testIntegration_AddRemoveFindOperations() {
    repository.add(lead1);
    repository.add(lead2);

    assertEquals(2, repository.findAll().size());

    Optional<Lead> found = repository.findById(lead1.id());
    assertTrue(found.isPresent());
    assertEquals(lead1.company(), found.get().company());

    repository.remove(lead1.id());

    List<Lead> remaining = repository.findAll();
    assertEquals(1, remaining.size());
    assertEquals(lead2, remaining.get(0));

    Optional<Lead> deleted = repository.findById(lead1.id());
    assertFalse(deleted.isPresent());

    repository.add(lead3);

    List<Lead> finalList = repository.findAll();
    assertEquals(2, finalList.size());
    assertTrue(finalList.contains(lead2));
    assertTrue(finalList.contains(lead3));
  }
}