package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.dto.LeadResponse;

@Mapper
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(
      target = "company",
      expression = "java(new Company(dto.getCompanyName(), dto.getIndustry()))")
  Lead toEntity(LeadFormDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateEntity(LeadFormDto dto, @MappingTarget Lead lead);

  @Mapping(target = "companyName", source = "company.name")
  LeadResponse toResponse(Lead entity);

  @AfterMapping
  default void afterUpdateEntity(LeadFormDto dto, @MappingTarget Lead lead) {
    lead.setEmail(dto.getEmail());
    lead.setStatus(dto.getStatus());
    lead.setCompany(new Company(dto.getCompanyName(), dto.getIndustry()));
  }

  @ObjectFactory
  default Lead createLead(LeadFormDto dto) {
    return new Lead(dto.getEmail(), null, dto.getStatus());
  }
}
