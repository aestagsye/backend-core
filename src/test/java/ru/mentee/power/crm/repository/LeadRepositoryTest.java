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
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LeadRepositoryTest {

  @Autowired
  private LeadRepository leadRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    leadRepository.deleteAll();
    companyRepository.deleteAll();
  }

  @Test
  void shouldFindByIdForUpdate() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead = leadRepository.save(new Lead("lock@test.com", company, LeadStatus.NEW));

    Optional<Lead> found = leadRepository.findByIdForUpdate(lead.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("lock@test.com");
  }

  @Test
  void shouldReturnEmptyForNonExistentLeadForUpdate() {
    Optional<Lead> found = leadRepository.findByIdForUpdate(UUID.randomUUID());

    assertThat(found).isEmpty();
  }

  @Test
  void shouldCountByStatus() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    leadRepository.save(new Lead("a@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("b@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("c@test.com", company, LeadStatus.CONTACTED));

    long newCount = leadRepository.countByStatus(LeadStatus.NEW);
    long contactedCount = leadRepository.countByStatus(LeadStatus.CONTACTED);

    assertThat(newCount).isEqualTo(2);
    assertThat(contactedCount).isEqualTo(1);
  }

  @Test
  void shouldFindByStatusIn() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    leadRepository.save(new Lead("a@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("b@test.com", company, LeadStatus.CONTACTED));
    leadRepository.save(new Lead("c@test.com", company, LeadStatus.QUALIFIED));

    List<Lead> result = leadRepository.findByStatusIn(List.of(LeadStatus.NEW, LeadStatus.CONTACTED));

    assertThat(result).hasSize(2);
  }

  @Test
  void shouldFindByCompanyName() {
    Company company = companyRepository.save(new Company("Acme Corp", "Tech"));
    leadRepository.save(new Lead("a@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("b@test.com", company, LeadStatus.CONTACTED));

    Page<Lead> result = leadRepository.findByCompanyName("Acme Corp", PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldUpdateStatusBulk() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    leadRepository.save(new Lead("a@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("b@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("c@test.com", company, LeadStatus.CONTACTED));

    int updated = leadRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);

    assertThat(updated).isEqualTo(2);
    assertThat(leadRepository.countByStatus(LeadStatus.CONTACTED)).isEqualTo(3);
  }

  @Test
  void shouldDeleteByStatusBulk() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    leadRepository.save(new Lead("a@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("b@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("c@test.com", company, LeadStatus.CONTACTED));

    int deleted = leadRepository.deleteByStatusBulk(LeadStatus.NEW);

    assertThat(deleted).isEqualTo(2);
    assertThat(leadRepository.countByStatus(LeadStatus.NEW)).isEqualTo(0);
  }

  @Test
  void shouldSaveAndFindLead() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead = new Lead("test@test.com", company, LeadStatus.NEW);

    Lead saved = leadRepository.save(lead);

    Optional<Lead> found = leadRepository.findById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@test.com");
  }

  @Test
  void shouldDeleteLead() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    Lead lead = leadRepository.save(new Lead("delete@test.com", company, LeadStatus.NEW));

    leadRepository.delete(lead);

    assertThat(leadRepository.findById(lead.getId())).isEmpty();
  }

  @Test
  void shouldFindAllLeads() {
    Company company = companyRepository.save(new Company("Test Co", "Tech"));
    leadRepository.save(new Lead("a@test.com", company, LeadStatus.NEW));
    leadRepository.save(new Lead("b@test.com", company, LeadStatus.NEW));

    List<Lead> all = leadRepository.findAll();

    assertThat(all).hasSize(2);
  }
}
