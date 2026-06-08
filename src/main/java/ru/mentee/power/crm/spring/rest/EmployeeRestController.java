package ru.mentee.power.crm.spring.rest;

/*
Реализовать 4 REST метода для управления сотрудниками.

У нас должна быть возможность
добавить сотрудника, DONE
изменить зарплату сотруднику,
 получить всех сотрудников, done
 а так же удалить сотрудника. DONE

БД - H2
Так же необходимо добавить скрипты базы данных для создания таблицы сотрудников:

id, name, salary

Для помощи в реализации можно использовать любые интернет ресурсы. Для тестирования REST API можно использовать postman. UI не обязателен
 */

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.EmployeeResponse;
import ru.mentee.power.crm.service.EmployeeService;
import ru.mentee.power.crm.spring.mapper.EmployeeMapper;

@RequestMapping("/api/employees")
@RequiredArgsConstructor
@RestController
public class EmployeeRestController {
  private final EmployeeService employeeService;
  private final EmployeeMapper employeeMapper;

  @GetMapping
  public ResponseEntity<List<EmployeeResponse>> getEmployees() {
    List<EmployeeResponse> employees = employeeService.findAll()
            .stream()
            .map(employeeMapper::toResponse).toList();
    return ResponseEntity.ok(employees);
  }

  @PostMapping
  public ResponseEntity<EmployeeResponse> addEmployee(@Valid Employee employee) {
    Employee created = employeeService.addEmployee(employee);
    URI location = URI.create("/api/employees/" + created.getId());
    return ResponseEntity.created(location).body(employeeMapper.toResponse(created));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<EmployeeResponse> updateSalary(
          @Valid BigDecimal salary, @PathVariable UUID id
  ) {
    employeeService.updateSalary(salary, id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable UUID id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }


}
