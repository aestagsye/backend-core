package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.EmployeeResponse;
import ru.mentee.power.crm.repository.EmployeeRepository;

@RequiredArgsConstructor
@Service
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  public List<Employee> findAll() {
    return employeeRepository.findAll();
  }

  @Transactional
  public void deleteEmployee(UUID id) {
    employeeRepository.deleteById(id);
  }

  @Transactional
  public Employee addEmployee(Employee employee) {
    employeeRepository.save(employee);
    return employee;
  }

  @Transactional
  public Employee updateSalary(BigDecimal salary, UUID id) {
    Employee employee = employeeRepository.findById(id).orElse(null);
    if (employee == null) {
      throw new IllegalStateException("Employee with id " + id + " not found");
    }
    employee.setSalary(salary);
    return employeeRepository.save(employee);
  }
}
