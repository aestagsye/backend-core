package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@Transactional
class LeadLockingServiceAdditionalTest {

  @Autowired private LeadLockingService leadLockingService;

  @Autowired private LeadRepository leadRepository;

  @Autowired private CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    leadRepository.deleteAll();
    companyRepository.deleteAll();
  }

  @Test
  void shouldConvertLeadToDealWithLock() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead = leadRepository.save(new Lead("lock@test.com", company, LeadStatus.NEW));

    Lead updated = leadLockingService.convertLeadToDealWithLock(lead.getId(), LeadStatus.QUALIFIED);

    assertThat(updated.getStatus()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldThrowException_whenLeadNotFoundForPessimisticLock() {
    assertThatThrownBy(
            () ->
                leadLockingService.convertLeadToDealWithLock(
                    UUID.randomUUID(), LeadStatus.QUALIFIED))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Lead not found");
  }

  @Test
  void shouldUpdateLeadStatusOptimistic() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead = leadRepository.save(new Lead("optimistic@test.com", company, LeadStatus.NEW));

    Lead updated =
        leadLockingService.updateLeadStatusOptimistic(lead.getId(), LeadStatus.CONTACTED);

    assertThat(updated.getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldThrowException_whenLeadNotFoundForOptimisticUpdate() {
    assertThatThrownBy(
            () ->
                leadLockingService.updateLeadStatusOptimistic(
                    UUID.randomUUID(), LeadStatus.CONTACTED))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Lead not found");
  }

  @Test
  void shouldUpdateWithRetrySuccess() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead = leadRepository.save(new Lead("retry@test.com", company, LeadStatus.NEW));

    Lead updated = leadLockingService.updateWithRetry(lead.getId(), LeadStatus.CONTACTED);

    assertThat(updated.getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldProcessLeadsInOrder() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead1 = leadRepository.save(new Lead("lead1@test.com", company, LeadStatus.NEW));
    Lead lead2 = leadRepository.save(new Lead("lead2@test.com", company, LeadStatus.NEW));

    leadLockingService.processLeadsInOrder(List.of(lead1.getId(), lead2.getId()));

    Lead updated1 = leadRepository.findById(lead1.getId()).orElseThrow();
    Lead updated2 = leadRepository.findById(lead2.getId()).orElseThrow();
    assertThat(updated1.getStatus()).isEqualTo(LeadStatus.CONTACTED);
    assertThat(updated2.getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldThrowException_whenProcessingNonExistentLeadInOrder() {
    assertThatThrownBy(() -> leadLockingService.processLeadsInOrder(List.of(UUID.randomUUID())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Lead not found");
  }
}
