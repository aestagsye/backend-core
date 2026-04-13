package ru.mentee.power.crm.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateDealRequestTest {

  @Test
  void shouldCreateRequest() {
    UUID companyId = UUID.randomUUID();
    BigDecimal amount = BigDecimal.valueOf(10000);

    CreateDealRequest request = new CreateDealRequest(amount, companyId);

    assertThat(request.getAmount()).isEqualByComparingTo(amount);
    assertThat(request.getCompanyId()).isEqualTo(companyId);
  }
}
