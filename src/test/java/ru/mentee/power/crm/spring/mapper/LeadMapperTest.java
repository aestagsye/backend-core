package ru.mentee.power.crm.spring.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.dto.LeadResponse;

@SpringBootTest
class LeadMapperTest {

  @Autowired private LeadMapper leadMapper;

  @Test
  void shouldMapCreateRequestToEntity_whenValidData() {
    // given
    LeadFormDto leadFormDto = new LeadFormDto();
    leadFormDto.setCompanyName("Company Name");
    leadFormDto.setEmail("s@s.com");
    leadFormDto.setIndustry("Industry");
    leadFormDto.setStatus(LeadStatus.NEW);
    // when
    Lead lead = leadMapper.toEntity(leadFormDto);
    // then
    assertThat(lead.getId()).isNull();
    assertThat(lead.getCompany().getName()).isEqualTo("Company Name");
    assertThat(lead.getCompany().getIndustry()).isEqualTo("Industry");
    assertThat(lead.getCreatedAt()).isNull();
    assertThat(lead.getStatus()).isEqualTo(LeadStatus.NEW);
    assertThat(lead.getEmail()).isEqualTo("s@s.com");
  }

  @Test
  void shouldMapEntityToResponse_whenValidEntity() {
    // given
    UUID id = UUID.randomUUID();
    Company acme = new Company("Acme", "BigIndustry");
    Lead lead = new Lead(id, "s@s.com", acme, LeadStatus.NEW, LocalDateTime.now());
    // when
    LeadResponse response = leadMapper.toResponse(lead);
    // then
    assertThat(response.companyName()).isEqualTo("Acme");
    assertThat(response.id()).isEqualTo(id);
    assertThat(response.createdAt()).isNotNull();
    assertThat(response.status()).isEqualTo(LeadStatus.NEW);
    assertThat(response.email()).isEqualTo("s@s.com");
  }
}
