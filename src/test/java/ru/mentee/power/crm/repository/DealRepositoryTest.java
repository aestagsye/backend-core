package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DealRepositoryTest {

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
  void shouldSaveAndFindDeal() {
    Deal deal = new Deal(qualifiedLead.getId(), java.math.BigDecimal.valueOf(10000));

    Deal saved = dealRepository.save(deal);

    Deal found = dealRepository.findById(saved.getId()).orElseThrow();
    assertThat(found.getLeadId()).isEqualTo(qualifiedLead.getId());
    assertThat(found.getAmount()).isEqualByComparingTo(java.math.BigDecimal.valueOf(10000));
    assertThat(found.getStatus()).isEqualTo(DealStatus.NEW);
  }

  @Test
  void shouldFindAllDeals() {
    dealRepository.save(new Deal(qualifiedLead.getId(), java.math.BigDecimal.valueOf(10000)));
    dealRepository.save(new Deal(qualifiedLead.getId(), java.math.BigDecimal.valueOf(20000)));

    List<Deal> deals = dealRepository.findAll();

    assertThat(deals).hasSize(2);
  }

  @Test
  void shouldDeleteDeal() {
    Deal deal = dealRepository.save(new Deal(qualifiedLead.getId(), java.math.BigDecimal.valueOf(10000)));

    dealRepository.delete(deal);

    assertThat(dealRepository.findById(deal.getId())).isEmpty();
  }
}
