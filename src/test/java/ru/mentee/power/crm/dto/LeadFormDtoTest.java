package ru.mentee.power.crm.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.LeadStatus;

class LeadFormDtoTest {

  @Test
  void shouldCreateAndPopulateDto() {
    LeadFormDto dto = new LeadFormDto();
    dto.setEmail("test@test.com");
    dto.setCompanyName("Test Company");
    dto.setIndustry("Tech");
    dto.setStatus(LeadStatus.NEW);

    assertThat(dto.getEmail()).isEqualTo("test@test.com");
    assertThat(dto.getCompanyName()).isEqualTo("Test Company");
    assertThat(dto.getIndustry()).isEqualTo("Tech");
    assertThat(dto.getStatus()).isEqualTo(LeadStatus.NEW);
  }
}
