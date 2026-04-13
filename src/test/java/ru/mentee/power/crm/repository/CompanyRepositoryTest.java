package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompanyRepositoryTest {

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private LeadRepository leadRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  void shouldSaveCompanyWithLeads() {
    // Given
    Company company = new Company("Сбербанк", "Finance");

    Lead lead1 = new Lead("ivan@sber.ru", company, LeadStatus.NEW);
    Lead lead2 = new Lead("maria@sber.ru", company, LeadStatus.CONTACTED);

    company.addLead(lead1);
    company.addLead(lead2);

    // When
    Company saved = companyRepository.save(company);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getLeads()).hasSize(2);

    // Проверяем, что в БД создались записи
    Company found = companyRepository.findById(saved.getId()).orElseThrow();
    assertThat(found.getLeads()).hasSize(2);
  }

  @Test
  void shouldAvoidN1WithEntityGraph() {
    // Given — создаём компанию с 5 лидами
    Company company = new Company("Тинькофф", "Finance");
    for (int i = 0; i < 5; i++) {
      company.addLead(new Lead("lead" + i + "@tinkoff.ru", company, LeadStatus.NEW));
    }
    Company saved = companyRepository.save(company);
    entityManager.flush();

    // Очищаем Persistence Context для чистоты эксперимента
    entityManager.clear();

    // When — используем метод с @EntityGraph
    Company found = companyRepository.findByIdWithLeads(saved.getId())
            .orElseThrow(() -> new AssertionError("Company not found with id: " + saved.getId()));

    // Then — проверяем, что leads загружены
    assertThat(found.getLeads()).hasSize(5);

    // Проверьте SQL логи: должен быть 1 запрос с LEFT JOIN,
    // а не 1 SELECT для Company + 5 SELECT для каждого Lead
  }
}