package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
class LeadLockingServiceTest {

  @Autowired private LeadLockingService leadLockingService;

  @Autowired private LeadRepository leadRepository;

  @Autowired private CompanyRepository companyRepository;

  private Company saveCompany(String name, String industry) {
    Company company = new Company(name, industry);
    return companyRepository.save(company);
  }

  private Lead saveLead(String email, Company company, LeadStatus status) {
    Lead lead = new Lead(email, company, status);
    return leadRepository.save(lead);
  }

  @Test
  void shouldPreventLostUpdate_whenPessimisticLockUsed() throws Exception {
    // Given: Lead с начальным статусом
    leadRepository.deleteAll();
    companyRepository.deleteAll();

    Company company = saveCompany("ACME Lock", "Acme Industries");
    Lead lead = saveLead("concurrent@test.com", company, LeadStatus.NEW);
    UUID leadId = lead.getId();

    // When: Два потока одновременно обновляют Lead с pessimistic lock
    ExecutorService executor = Executors.newFixedThreadPool(2);

    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(2);

    Future<LeadStatus> task1 =
        executor.submit(
            () -> {
              startLatch.await(); // Синхронизируем старт
              Lead updated =
                  leadLockingService.convertLeadToDealWithLock(leadId, LeadStatus.CONTACTED);
              doneLatch.countDown();
              return updated.getStatus();
            });

    Future<LeadStatus> task2 =
        executor.submit(
            () -> {
              startLatch.await();
              Lead updated =
                  leadLockingService.convertLeadToDealWithLock(leadId, LeadStatus.QUALIFIED);
              doneLatch.countDown();
              return updated.getStatus();
            });

    startLatch.countDown(); // Запускаем оба потока одновременно
    doneLatch.await(10, TimeUnit.SECONDS); // Ждём завершения

    // Then: Оба обновления успешны, вторая транзакция ждала первую
    LeadStatus status1 = task1.get();
    LeadStatus status2 = task2.get();

    assertThat(status1).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);
    assertThat(status2).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);
    assertThat(status1).isNotEqualTo(status2); // Разные статусы (не должны быть)

    // Финальный статус — последняя commit'нутая транзакция
    Lead finalLead = leadRepository.findById(leadId).orElseThrow();
    assertThat(finalLead.getStatus()).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);

    executor.shutdown();
  }

  @Test
  void shouldThrowOptimisticLockException_whenConcurrentUpdateWithoutLock() throws Exception {
    // Given
    leadRepository.deleteAll();
    companyRepository.deleteAll();

    Company company = saveCompany("ACME Opt", "Acme Industries");
    Lead lead = saveLead("optimistic@test.com", company, LeadStatus.NEW);
    UUID leadId = lead.getId();

    ExecutorService executor = Executors.newFixedThreadPool(2);

    CountDownLatch readyLatch = new CountDownLatch(2); // оба потока готовы
    CountDownLatch startLatch = new CountDownLatch(1); // одновременный старт
    CountDownLatch doneLatch = new CountDownLatch(2); // оба завершили

    Future<?> task1 =
        executor.submit(
            () -> {
              readyLatch.countDown();
              startLatch.await(); // ждём сигнала
              leadLockingService.updateLeadStatusOptimistic(leadId, LeadStatus.CONTACTED);
              doneLatch.countDown();
              return null;
            });

    Future<?> task2 =
        executor.submit(
            () -> {
              readyLatch.countDown();
              startLatch.await();
              leadLockingService.updateLeadStatusOptimistic(leadId, LeadStatus.QUALIFIED);
              doneLatch.countDown();
              return null;
            });

    readyLatch.await(); // убеждаемся, что оба потока готовы
    startLatch.countDown(); // даём команду на старт
    doneLatch.await(5, TimeUnit.SECONDS);

    boolean exceptionThrown = false;
    try {
      task1.get();
      task2.get();
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(ObjectOptimisticLockingFailureException.class);
      exceptionThrown = true;
    }

    assertThat(exceptionThrown).isTrue();
    executor.shutdown();
  }

  @Test
  void shouldDeadLock() throws Exception {
    leadRepository.deleteAll();
    companyRepository.deleteAll();

    Company companyA = saveCompany("CompanyA", "A Industries");
    Company companyB = saveCompany("CompanyB", "B Industries");

    Lead leadA = saveLead("a@test.com", companyA, LeadStatus.NEW);
    Lead leadB = saveLead("b@test.com", companyB, LeadStatus.NEW);

    List<UUID> order1 = List.of(leadA.getId(), leadB.getId());
    List<UUID> order2 = List.of(leadB.getId(), leadA.getId());

    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch startLatch = new CountDownLatch(1);

    Future<?> task1 =
        executor.submit(
            () -> {
              try {
                startLatch.await();
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
              leadLockingService.processLeadsInOrder(order1);
            });
    Future<?> task2 =
        executor.submit(
            () -> {
              try {
                startLatch.await();
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
              leadLockingService.processLeadsInOrder(order2);
            });

    startLatch.countDown();
    boolean deadlockDetected = false;
    try {
      task1.get(10, TimeUnit.SECONDS);
      task2.get(10, TimeUnit.SECONDS);
    } catch (ExecutionException e) {

      if (e.getCause() instanceof org.springframework.dao.CannotAcquireLockException) {
        deadlockDetected = true;
      }
    }

    assertThat(deadlockDetected).isTrue();
    executor.shutdown();
  }
}
