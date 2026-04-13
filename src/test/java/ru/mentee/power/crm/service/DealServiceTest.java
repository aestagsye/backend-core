package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DealServiceTest {

  @Autowired
  private DealService dealService;

  @Autowired
  private LeadService leadService;

  @Autowired
  private DealRepository dealRepository;

  @Autowired
  private LeadRepository leadRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private Lead qualifiedLead;

  @BeforeEach
  void setUp() {
    dealRepository.deleteAll();
    leadRepository.deleteAll();
    companyRepository.deleteAll();

    Company company = companyRepository.save(new Company("Test Company", "Tech"));
    qualifiedLead = leadRepository.save(new Lead("qualified@test.com", company, LeadStatus.QUALIFIED));
  }

  @Test
  void shouldTransitionDealStatus() {
    Deal deal = leadService.convertLeadToDeal(qualifiedLead.getId(), new CreateDealRequest(BigDecimal.valueOf(10000), qualifiedLead.getCompany().getId()));

    Deal updated = dealService.transitionDealStatus(deal.getId(), DealStatus.QUALIFIED);

    assertThat(updated.getStatus()).isEqualTo(DealStatus.QUALIFIED);
  }

  @Test
  void shouldThrowException_whenDealNotFound() {
    UUID nonExistentId = UUID.randomUUID();

    assertThatThrownBy(() -> dealService.transitionDealStatus(nonExistentId, DealStatus.QUALIFIED))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Deal not found");
  }

  @Test
  void shouldGetAllDeals() {
    leadService.convertLeadToDeal(qualifiedLead.getId(), new CreateDealRequest(BigDecimal.valueOf(5000), qualifiedLead.getCompany().getId()));

    List<Deal> deals = dealService.getAllDeals();

    assertThat(deals).hasSize(1);
    assertThat(deals.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
  }

  @Test
  void shouldGetDealsByStatusForKanban() {
    Company company2 = companyRepository.save(new Company("Another Company", "Retail"));
    Lead lead2 = leadRepository.save(new Lead("lead2@test.com", company2, LeadStatus.QUALIFIED));

    Deal deal1 = leadService.convertLeadToDeal(qualifiedLead.getId(), new CreateDealRequest(BigDecimal.valueOf(10000), qualifiedLead.getCompany().getId()));
    Deal deal2 = leadService.convertLeadToDeal(lead2.getId(), new CreateDealRequest(BigDecimal.valueOf(20000), company2.getId()));

    dealService.transitionDealStatus(deal1.getId(), DealStatus.QUALIFIED);

    Map<DealStatus, List<Deal>> dealsByStatus = dealService.getDealsByStatusForKanban();

    assertThat(dealsByStatus).containsKey(DealStatus.NEW);
    assertThat(dealsByStatus).containsKey(DealStatus.QUALIFIED);
    assertThat(dealsByStatus.get(DealStatus.QUALIFIED)).hasSize(1);
  }

  @Test
  void shouldThrowException_whenTransitionIsInvalid() {
    Deal deal = leadService.convertLeadToDeal(qualifiedLead.getId(), new CreateDealRequest(BigDecimal.valueOf(10000), qualifiedLead.getCompany().getId()));
    dealService.transitionDealStatus(deal.getId(), DealStatus.QUALIFIED);
    dealService.transitionDealStatus(deal.getId(), DealStatus.PROPOSAL_SENT);

    assertThatThrownBy(() -> dealService.transitionDealStatus(deal.getId(), DealStatus.NEW))
            .isInstanceOf(IllegalStateException.class);
  }
}
