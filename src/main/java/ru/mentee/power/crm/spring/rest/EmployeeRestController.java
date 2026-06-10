package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.dto.EmployeeResponse;
import ru.mentee.power.crm.dto.UpdateSalaryRequest;
import ru.mentee.power.crm.service.EmployeeService;
import ru.mentee.power.crm.spring.mapper.EmployeeMapper;

@RequestMapping("/api/employees")
@RequiredArgsConstructor
@RestController
public class EmployeeRestController {
  private final EmployeeService employeeService;
  private final EmployeeMapper employeeMapper;

  @GetMapping
  public ResponseEntity<Page<EmployeeResponse>> getEmployees(Pageable pageable) {
    Page<EmployeeResponse> employees = employeeService.findAll(pageable);
    return ResponseEntity.ok(employees);
  }

  @PostMapping
  public ResponseEntity<EmployeeResponse> addEmployee(
      @Valid @RequestBody CreateEmployeeRequest request) {
    Employee created = employeeService.addEmployee(request);
    URI location = URI.create("/api/employees/" + created.getId());
    return ResponseEntity.created(location).body(employeeMapper.toResponse(created));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<EmployeeResponse> updateSalary(
      @Valid @RequestBody UpdateSalaryRequest request, @PathVariable UUID id) {
    Employee patched = employeeService.updateSalary(request, id);
    return ResponseEntity.ok(employeeMapper.toResponse(patched));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }
}
