package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.EmployeeResponse;

@Mapper
public interface EmployeeMapper {
  EmployeeResponse toResponse(Employee entity);
}
