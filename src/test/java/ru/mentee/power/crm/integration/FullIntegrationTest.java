package ru.mentee.power.crm.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.CreateDealRequest;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.exception.IllegalLeadStateException;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadLockingService;
import ru.mentee.power.crm.service.LeadService;

@SpringBootTest
@Transactional
class FullIntegrationTest {

  @Autowired private LeadService leadService;

  @Autowired private DealService dealService;

  @Autowired private LeadLockingService leadLockingService;

  @Autowired private LeadRepository leadRepository;

  @Autowired private DealRepository dealRepository;

  @Autowired private CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    dealRepository.deleteAll();
    leadRepository.deleteAll();
    companyRepository.deleteAll();
  }

  @Test
  void shouldCreateLeadAndConvertToDeal() {
    Company company = companyRepository.save(new Company("Test Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("test@test.com", company, LeadStatus.NEW));

    lead.setStatus(LeadStatus.CONTACTED);
    leadRepository.save(lead);

    lead.setStatus(LeadStatus.QUALIFIED);
    leadRepository.save(lead);

    Deal deal =
        leadService.convertLeadToDeal(
            lead.getId(), new CreateDealRequest(BigDecimal.valueOf(50000), company.getId()));

    assertThat(deal).isNotNull();
    assertThat(deal.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
    assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);

    Lead updatedLead = leadRepository.findById(lead.getId()).orElseThrow();
    assertThat(updatedLead.getStatus()).isEqualTo(LeadStatus.CONVERTED);
  }

  @Test
  void shouldTransitionDealThroughWorkflow() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("lead@test.com", company, LeadStatus.QUALIFIED));
    Deal deal =
        leadService.convertLeadToDeal(
            lead.getId(), new CreateDealRequest(BigDecimal.valueOf(10000), company.getId()));

    Deal qualified = dealService.transitionDealStatus(deal.getId(), DealStatus.QUALIFIED);
    assertThat(qualified.getStatus()).isEqualTo(DealStatus.QUALIFIED);

    Deal proposal = dealService.transitionDealStatus(deal.getId(), DealStatus.PROPOSAL_SENT);
    assertThat(proposal.getStatus()).isEqualTo(DealStatus.PROPOSAL_SENT);

    Deal negotiation = dealService.transitionDealStatus(deal.getId(), DealStatus.NEGOTIATION);
    assertThat(negotiation.getStatus()).isEqualTo(DealStatus.NEGOTIATION);

    Deal won = dealService.transitionDealStatus(deal.getId(), DealStatus.WON);
    assertThat(won.getStatus()).isEqualTo(DealStatus.WON);
  }

  @Test
  void shouldFilterLeadsBySearchAndStatus() {
    Company company1 = companyRepository.save(new Company("Acme", "Tech"));
    Company company2 = companyRepository.save(new Company("Beta", "Retail"));

    leadRepository.save(new Lead("john@acme.com", company1, LeadStatus.NEW));
    leadRepository.save(new Lead("jane@acme.com", company1, LeadStatus.CONTACTED));
    leadRepository.save(new Lead("bob@beta.com", company2, LeadStatus.NEW));

    List<Lead> filteredByEmail = leadService.findLeads("john", null);
    assertThat(filteredByEmail).hasSize(1);
    assertThat(filteredByEmail.get(0).getEmail()).isEqualTo("john@acme.com");

    List<Lead> filteredByStatus = leadService.findLeads(null, LeadStatus.NEW);
    assertThat(filteredByStatus).hasSize(2);

    List<Lead> filteredBoth = leadService.findLeads("acme", LeadStatus.CONTACTED);
    assertThat(filteredBoth).hasSize(1);
  }

  @Test
  void shouldHandleBulkOperations() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    leadRepository.save(new Lead("lead1@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("lead2@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("lead3@test.com", company, LeadStatus.NEW));

    int converted = leadService.convertNewToContacted();
    assertThat(converted).isEqualTo(3);

    long contactedCount = leadRepository.countByStatus(LeadStatus.CONTACTED);
    assertThat(contactedCount).isEqualTo(3);
  }

  @Test
  void shouldArchiveLeads() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    leadRepository.save(new Lead("old1@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("old2@test.com", company, LeadStatus.NEW));

    int deleted = leadService.archiveOldLeads(LeadStatus.NEW);
    assertThat(deleted).isEqualTo(2);

    long remaining = leadRepository.count();
    assertThat(remaining).isEqualTo(0);
  }

  @Test
  void shouldThrowException_whenConvertingInvalidStatus() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead newLead = leadRepository.save(new Lead("new@test.com", company, LeadStatus.NEW));
    Lead contactedLead =
        leadRepository.save(new Lead("contacted@test.com", company, LeadStatus.CONTACTED));

    assertThatThrownBy(
            () ->
                leadService.convertLeadToDeal(
                    newLead.getId(), new CreateDealRequest(BigDecimal.TEN, company.getId())))
        .isInstanceOf(IllegalLeadStateException.class);

    assertThatThrownBy(
            () ->
                leadService.convertLeadToDeal(
                    contactedLead.getId(), new CreateDealRequest(BigDecimal.TEN, company.getId())))
        .isInstanceOf(IllegalLeadStateException.class);
  }

  @Test
  void shouldUpdateLeadFields() {
    Company company = companyRepository.save(new Company("Old Company", "Old Industry"));
    Lead lead = leadRepository.save(new Lead("old@email.com", company, LeadStatus.NEW));

    Company newCompany = companyRepository.save(new Company("New Company", "New Industry"));
    lead.setEmail("new@email.com");
    lead.setCompany(newCompany);
    lead.setStatus(LeadStatus.QUALIFIED);

    Lead updated = leadService.update(lead.getId(), lead);

    assertThat(updated.getEmail()).isEqualTo("new@email.com");
    assertThat(updated.getCompany().getName()).isEqualTo("New Company");
    assertThat(updated.getStatus()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldThrowException_whenUpdatingNonExistentLead() {
    Lead nonExistent = new Lead("test@test.com", new Company("Test", "Test"), LeadStatus.NEW);

    assertThatThrownBy(() -> leadService.update(UUID.randomUUID(), nonExistent))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldDeleteLead() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("delete@test.com", company, LeadStatus.NEW));

    UUID leadId = lead.getId();
    leadService.delete(leadId);

    assertThat(leadRepository.findById(leadId)).isEmpty();
  }

  @Test
  void shouldThrowException_whenDeletingNonExistentLead() {
    assertThatThrownBy(() -> leadService.delete(UUID.randomUUID()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void shouldUpdateLeadWithLock() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("lock@test.com", company, LeadStatus.NEW));

    Lead updated = leadLockingService.convertLeadToDealWithLock(lead.getId(), LeadStatus.QUALIFIED);

    assertThat(updated.getStatus()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldUpdateLeadOptimistically() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("optimistic@test.com", company, LeadStatus.NEW));

    Lead updated =
        leadLockingService.updateLeadStatusOptimistic(lead.getId(), LeadStatus.CONTACTED);

    assertThat(updated.getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldGetAllDeals() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead1 = leadRepository.save(new Lead("lead1@test.com", company, LeadStatus.QUALIFIED));
    Lead lead2 = leadRepository.save(new Lead("lead2@test.com", company, LeadStatus.QUALIFIED));

    leadService.convertLeadToDeal(
        lead1.getId(), new CreateDealRequest(BigDecimal.valueOf(10000), company.getId()));
    leadService.convertLeadToDeal(
        lead2.getId(), new CreateDealRequest(BigDecimal.valueOf(20000), company.getId()));

    List<Deal> allDeals = dealService.getAllDeals();
    assertThat(allDeals).hasSize(2);
  }

  @Test
  void shouldGetDealsGroupedByStatus() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead1 = leadRepository.save(new Lead("lead1@test.com", company, LeadStatus.QUALIFIED));
    Lead lead2 = leadRepository.save(new Lead("lead2@test.com", company, LeadStatus.QUALIFIED));

    Deal deal1 =
        leadService.convertLeadToDeal(
            lead1.getId(), new CreateDealRequest(BigDecimal.valueOf(10000), company.getId()));
    Deal deal2 =
        leadService.convertLeadToDeal(
            lead2.getId(), new CreateDealRequest(BigDecimal.valueOf(20000), company.getId()));

    dealService.transitionDealStatus(deal1.getId(), DealStatus.QUALIFIED);

    var dealsByStatus = dealService.getDealsByStatusForKanban();

    assertThat(dealsByStatus).containsKey(DealStatus.NEW);
    assertThat(dealsByStatus).containsKey(DealStatus.QUALIFIED);
  }

  @Test
  void shouldFindByStatuses() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    leadRepository.save(new Lead("s1@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("s2@test.com", company, LeadStatus.CONTACTED));
    leadRepository.save(new Lead("s3@test.com", company, LeadStatus.QUALIFIED));

    List<Lead> result = leadService.findByStatuses(LeadStatus.NEW, LeadStatus.CONTACTED);

    assertThat(result).hasSize(2);
  }

  @Test
  void shouldGetFirstPage() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    for (int i = 0; i < 10; i++) {
      leadRepository.save(new Lead("page" + i + "@test.com", company, LeadStatus.NEW));
    }

    Page<Lead> page = leadService.getFirstPage(5);

    assertThat(page.getSize()).isEqualTo(5);
    assertThat(page.getTotalElements()).isEqualTo(10);
  }

  @Test
  void shouldSearchByCompany() {
    Company company = companyRepository.save(new Company("SearchMe", "Tech"));
    leadRepository.save(new Lead("search1@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("search2@test.com", company, LeadStatus.CONTACTED));

    Page<Lead> result = leadService.searchByCompany("SearchMe", 0, 10);

    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldThrowException_whenProcessingLeadWithExceptionEmail() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("throw-exception@test.com", company, LeadStatus.NEW));

    assertThatThrownBy(() -> leadService.processSingleLead(lead.getId()))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void shouldThrowException_whenProcessingNonExistentLead() {
    assertThatThrownBy(() -> leadService.processSingleLead(UUID.randomUUID()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowException_whenDealTransitionInvalid() {
    Company company = companyRepository.save(new Company("Company", "Tech"));
    Lead lead = leadRepository.save(new Lead("deal@test.com", company, LeadStatus.QUALIFIED));
    Deal deal =
        leadService.convertLeadToDeal(
            lead.getId(), new CreateDealRequest(BigDecimal.TEN, company.getId()));

    dealService.transitionDealStatus(deal.getId(), DealStatus.QUALIFIED);
    dealService.transitionDealStatus(deal.getId(), DealStatus.PROPOSAL_SENT);

    assertThatThrownBy(() -> dealService.transitionDealStatus(deal.getId(), DealStatus.NEW))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowException_whenDealNotFound() {
    assertThatThrownBy(
            () -> dealService.transitionDealStatus(UUID.randomUUID(), DealStatus.QUALIFIED))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
