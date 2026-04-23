package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DealStatusTest {

  @ParameterizedTest(name = "{0} -> {1} should be {2}")
  @MethodSource("provideTransitionCases")
  void shouldAllowOrDenyTransition(DealStatus from, DealStatus to, boolean shouldAllow) {
    boolean canTransition = from.canTransitionTo(to);

    assertThat(canTransition).isEqualTo(shouldAllow);
  }

  static Stream<Arguments> provideTransitionCases() {
    return Stream.of(
        Arguments.of(DealStatus.NEW, DealStatus.QUALIFIED, true),
        Arguments.of(DealStatus.NEW, DealStatus.LOST, true),
        Arguments.of(DealStatus.QUALIFIED, DealStatus.PROPOSAL_SENT, true),
        Arguments.of(DealStatus.QUALIFIED, DealStatus.LOST, true),
        Arguments.of(DealStatus.PROPOSAL_SENT, DealStatus.NEGOTIATION, true),
        Arguments.of(DealStatus.PROPOSAL_SENT, DealStatus.LOST, true),
        Arguments.of(DealStatus.NEGOTIATION, DealStatus.WON, true),
        Arguments.of(DealStatus.NEGOTIATION, DealStatus.LOST, true),
        Arguments.of(DealStatus.WON, DealStatus.NEW, false),
        Arguments.of(DealStatus.LOST, DealStatus.QUALIFIED, false),
        Arguments.of(DealStatus.NEW, DealStatus.WON, false),
        Arguments.of(DealStatus.NEW, DealStatus.NEGOTIATION, false));
  }
}
