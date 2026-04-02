package ru.mentee.power.crm.domain;

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

    assertThat(deal.getId()).isNull();
    assertThat(deal.getLeadId()).isEqualTo(leadId);
    assertThat(deal.getAmount()).isEqualTo(amount);
    assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);
    assertThat(deal.getCreatedAt()).isNull();
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

  @Test
  void shouldSetValues() {
    //given
    Deal deal = new Deal(UUID.randomUUID(),UUID.randomUUID(),
            new BigDecimal(500), DealStatus.WON, LocalDateTime.now());
    //when
    UUID id = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();
    deal.setId(id);
    deal.setAmount(new BigDecimal(1000));
    deal.setLeadId(leadId);
    deal.setCreatedAt(LocalDateTime.MIN);
    deal.setStatus(DealStatus.QUALIFIED);
    //then
    assertThat(deal.getId()).isEqualTo(id);
    assertThat(deal.getLeadId()).isEqualTo(leadId);
    assertThat(deal.getAmount()).isEqualTo(new BigDecimal(1000));
    assertThat(deal.getCreatedAt()).isEqualTo(LocalDateTime.MIN);
    assertThat(deal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
  }
}