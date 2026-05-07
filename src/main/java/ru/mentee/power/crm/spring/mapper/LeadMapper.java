package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.dto.LeadResponse;

@Mapper // componentModel и unmappedTargetPolicy заданы глобально в compilerArgs
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(
      target = "company",
      expression = "java(new Company(dto.getCompanyName(), dto.getIndustry()))")
  Lead toEntity(LeadFormDto dto);

  @Mapping(target = "companyName", source = "company.name")
  LeadResponse toResponse(Lead entity);

  default void updateEntity(LeadFormDto dto, @MappingTarget Lead lead) {
    // MapStruct часто сам умеет обновление, но здесь оставляем явное обновление,
    // чтобы гарантированно проставить company через DTO.
    lead.setEmail(dto.getEmail());
    lead.setStatus(dto.getStatus());
    lead.setCompany(new Company(dto.getCompanyName(), dto.getIndustry()));
  }

  @ObjectFactory
  default Lead createLead(LeadFormDto dto) {
    return new Lead(dto.getEmail(), null, dto.getStatus());
  }
}
