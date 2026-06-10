package ru.mentee.power.crm.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.dto.EmployeeResponse;
import ru.mentee.power.crm.dto.UpdateSalaryRequest;
import ru.mentee.power.crm.repository.EmployeeRepository;
import ru.mentee.power.crm.spring.mapper.EmployeeMapper;
import ru.mentee.power.crm.spring.restexception.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper mapper;

  public Page<EmployeeResponse> findAll(Pageable pageable) {
    return employeeRepository.findAll(pageable).map(mapper::toResponse);
  }

  @Transactional
  public void deleteEmployee(UUID id) {
    employeeRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Employee with id: " + id + " not found"));
    employeeRepository.deleteById(id);
  }

  @Transactional
  public Employee addEmployee(CreateEmployeeRequest request) {
    Employee created = new Employee(request.name(), request.salary());
    employeeRepository.save(created);
    return created;
  }

  @Transactional
  public Employee updateSalary(UpdateSalaryRequest request, UUID id) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
    employee.setSalary(request.salary());
    return employeeRepository.save(employee);
  }
}
