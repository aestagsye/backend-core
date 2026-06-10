package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Employee;
import ru.mentee.power.crm.dto.EmployeeResponse;
import ru.mentee.power.crm.service.EmployeeService;
import ru.mentee.power.crm.spring.mapper.EmployeeMapperImpl;
import ru.mentee.power.crm.spring.restexception.EntityNotFoundException;

@WebMvcTest(EmployeeRestController.class)
@Import(EmployeeMapperImpl.class)
class EmployeeRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private EmployeeService employeeService;

  @Test
  void shouldReturn200WithPagedEmployees() throws Exception {
    UUID id = UUID.randomUUID();
    EmployeeResponse employee = new EmployeeResponse(id, "Ivan Petrov", BigDecimal.valueOf(150000));
    PageRequest pageable = PageRequest.of(0, 20);
    when(employeeService.findAll(pageable)).thenReturn(new PageImpl<>(java.util.List.of(employee)));

    mockMvc
        .perform(get("/api/employees").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].id").value(id.toString()))
        .andExpect(jsonPath("$.content[0].name").value("Ivan Petrov"))
        .andExpect(jsonPath("$.content[0].salary").value(150000));
  }

  @Test
  void shouldReturn201WithLocationWhenCreateEmployee() throws Exception {
    UUID id = UUID.randomUUID();
    Employee created = new Employee("Ivan Petrov", BigDecimal.valueOf(150000));
    created.setId(id);
    when(employeeService.addEmployee(any())).thenReturn(created);

    mockMvc
        .perform(
            post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "Ivan Petrov",
                      "salary": 150000
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, "/api/employees/" + id))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Ivan Petrov"))
        .andExpect(jsonPath("$.salary").value(150000));
  }

  @Test
  void shouldReturn400WhenCreateRequestIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "",
                      "salary": -1
                    }
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn200WhenUpdateSalary() throws Exception {
    UUID id = UUID.randomUUID();
    Employee updated = new Employee("Ivan Petrov", BigDecimal.valueOf(180000));
    updated.setId(id);
    when(employeeService.updateSalary(any(), eq(id))).thenReturn(updated);

    mockMvc
        .perform(
            patch("/api/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "salary": 180000
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.salary").value(180000));

    verify(employeeService).updateSalary(any(), eq(id));
  }

  @Test
  void shouldReturn400WhenUpdateSalaryRequestIsInvalid() throws Exception {
    mockMvc
        .perform(
            patch("/api/employees/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "salary": 0
                    }
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn404WhenUpdateMissingEmployee() throws Exception {
    UUID id = UUID.randomUUID();
    when(employeeService.updateSalary(any(), eq(id)))
        .thenThrow(new EntityNotFoundException("Employee", id.toString()));

    mockMvc
        .perform(
            patch("/api/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "salary": 180000
                    }
                    """))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn204WhenDeleteEmployee() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc
        .perform(delete("/api/employees/" + id))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));

    verify(employeeService).deleteEmployee(id);
  }

  @Test
  void shouldReturn404WhenDeleteMissingEmployee() throws Exception {
    UUID id = UUID.randomUUID();
    org.mockito.Mockito.doThrow(new EntityNotFoundException("Employee", id.toString()))
        .when(employeeService)
        .deleteEmployee(id);

    mockMvc.perform(delete("/api/employees/" + id)).andExpect(status().isNotFound());
  }
}
