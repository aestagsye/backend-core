package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompanyTest {

  @Test
  void shouldCreateCompanyWithAllFields() {
    String name = "Test Company";
    String industry = "Technology";

    Company company = new Company(name, industry);

    assertThat(company.getName()).isEqualTo(name);
    assertThat(company.getIndustry()).isEqualTo(industry);
  }

  @Test
  void shouldCreateDefaultCompany() {
    Company company = new Company();

    assertThat(company).isNotNull();
  }

  @Test
  void shouldSetAndGetFields() {
    Company company = new Company();
    company.setName("New Company");
    company.setIndustry("Retail");

    assertThat(company.getName()).isEqualTo("New Company");
    assertThat(company.getIndustry()).isEqualTo("Retail");
  }

  @Test
  void shouldCheckEqualityBasedOnId() {
    Company company1 = new Company("A", "Tech");
    Company company2 = new Company("B", "Retail");

    assertThat(company1).isNotEqualTo(company2);
  }
}
