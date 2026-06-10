package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.dto.EmployeeResponse;
import ru.mentee.power.crm.dto.UpdateSalaryRequest;
import ru.mentee.power.crm.repository.EmployeeRepository;
import ru.mentee.power.crm.spring.mapper.EmployeeMapper;
import ru.mentee.power.crm.spring.restexception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  @Mock private EmployeeRepository employeeRepository;

  @Mock private EmployeeMapper employeeMapper;

  @InjectMocks private EmployeeService employeeService;

  @Test
  void shouldReturnPagedEmployeeResponses() {
    Employee employee = new Employee("Ivan Petrov", BigDecimal.valueOf(150000));
    UUID id = UUID.randomUUID();
    employee.setId(id);
    PageRequest pageable = PageRequest.of(0, 10);
    EmployeeResponse response = new EmployeeResponse(id, "Ivan Petrov", BigDecimal.valueOf(150000));

    when(employeeRepository.findAll(pageable))
        .thenReturn(new PageImpl<>(java.util.List.of(employee)));
    when(employeeMapper.toResponse(employee)).thenReturn(response);

    Page<EmployeeResponse> result = employeeService.findAll(pageable);

    assertThat(result.getContent()).containsExactly(response);
  }

  @Test
  void shouldCreateEmployeeFromRequest() {
    CreateEmployeeRequest request =
        new CreateEmployeeRequest("Ivan Petrov", BigDecimal.valueOf(150000));
    ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);

    Employee created = employeeService.addEmployee(request);

    verify(employeeRepository).save(captor.capture());
    assertThat(captor.getValue()).isSameAs(created);
    assertThat(created.getName()).isEqualTo("Ivan Petrov");
    assertThat(created.getSalary()).isEqualByComparingTo("150000");
  }

  @Test
  void shouldUpdateSalaryWhenEmployeeExists() {
    UUID id = UUID.randomUUID();
    Employee employee = new Employee("Ivan Petrov", BigDecimal.valueOf(100000));
    UpdateSalaryRequest request = new UpdateSalaryRequest(BigDecimal.valueOf(180000));

    when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
    when(employeeRepository.save(any(Employee.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Employee updated = employeeService.updateSalary(request, id);

    assertThat(updated.getSalary()).isEqualByComparingTo("180000");
    verify(employeeRepository).save(employee);
  }

  @Test
  void shouldThrowNotFoundWhenUpdatingMissingEmployee() {
    UUID id = UUID.randomUUID();
    UpdateSalaryRequest request = new UpdateSalaryRequest(BigDecimal.valueOf(180000));
    when(employeeRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> employeeService.updateSalary(request, id))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Employee");
  }

  @Test
  void shouldDeleteExistingEmployee() {
    UUID id = UUID.randomUUID();
    Employee employee = new Employee("Ivan Petrov", BigDecimal.valueOf(100000));
    when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

    employeeService.deleteEmployee(id);

    verify(employeeRepository).deleteById(id);
  }

  @Test
  void shouldThrowNotFoundWhenDeletingMissingEmployee() {
    UUID id = UUID.randomUUID();
    when(employeeRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> employeeService.deleteEmployee(id))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(id.toString());
  }
}
