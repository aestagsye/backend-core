package ru.mentee.power.crm.spring.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.EmployeeResponse;

@SpringBootTest
class EmployeeMapperTest {

  @Autowired private EmployeeMapper employeeMapper;

  @Test
  void shouldMapEntityToResponse() {
    UUID id = UUID.randomUUID();
    Employee employee = new Employee("Ivan Petrov", BigDecimal.valueOf(150000));
    employee.setId(id);

    EmployeeResponse response = employeeMapper.toResponse(employee);

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.name()).isEqualTo("Ivan Petrov");
    assertThat(response.salary()).isEqualByComparingTo("150000");
  }
}
