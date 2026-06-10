package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Employee;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {

  @Autowired private EmployeeRepository employeeRepository;

  @BeforeEach
  void setUp() {
    employeeRepository.deleteAll();
  }

  @Test
  void shouldSaveAndFindEmployee() {
    Employee saved =
        employeeRepository.save(new Employee("Ivan Petrov", BigDecimal.valueOf(150000)));

    assertThat(saved.getId()).isNotNull();
    assertThat(employeeRepository.findById(saved.getId())).isPresent();
  }

  @Test
  void shouldReturnPagedEmployees() {
    employeeRepository.save(new Employee("Ivan Petrov", BigDecimal.valueOf(150000)));
    employeeRepository.save(new Employee("Petr Ivanov", BigDecimal.valueOf(160000)));

    Page<Employee> page = employeeRepository.findAll(PageRequest.of(0, 1));

    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent()).hasSize(1);
  }

  @Test
  void shouldDeleteEmployee() {
    Employee saved =
        employeeRepository.save(new Employee("Ivan Petrov", BigDecimal.valueOf(150000)));

    employeeRepository.deleteById(saved.getId());

    assertThat(employeeRepository.findById(saved.getId())).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenEmployeeNotFound() {
    assertThat(employeeRepository.findById(UUID.randomUUID())).isEmpty();
  }
}
