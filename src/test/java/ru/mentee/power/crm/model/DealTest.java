package ru.mentee.power.crm.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class DealTest {

  @Test
  void shouldCreateDeal_withNewStatus() {
    UUID leadId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("100000.00");

    Deal deal = new Deal(leadId, amount);

    assertThat(deal.getId()).isNotNull();
    assertThat(deal.getLeadId()).isEqualTo(leadId);
    assertThat(deal.getAmount()).isEqualTo(amount);
    assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);
    assertThat(deal.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldTransitionToValidStatus() {
    //given:
    Deal deal = new Deal(UUID.randomUUID(), new BigDecimal(500));
    //when:
    deal.transitionTo(DealStatus.QUALIFIED);
    //then:
    assertThat(deal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
  }

  @Test
  void shouldThrowException_whenTransitionInvalid() {
    //given:
    Deal deal = new Deal(UUID.randomUUID(),UUID.randomUUID(),
            new BigDecimal(500), DealStatus.WON, LocalDateTime.now());
    //when, then:
    assertThatThrownBy(() -> deal.transitionTo(DealStatus.NEW))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from WON to NEW");
  }
}