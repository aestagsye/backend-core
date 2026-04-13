package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.service.LeadService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadControllerUnitTest {

  @Mock
  private LeadService leadService;  // создаётся мок

  @InjectMocks
  private LeadController controller; // в контроллер автоматически внедряется мок

  @Test
  void shouldReturnHomeMessageWithLeadCount() {
    // Given: мок возвращает список из 2 лидов
    List<Lead> mockLeads = List.of(new Lead("",null, null),
            new Lead("", null, null)); // можно создать реальные объекты
    when(leadService.findAll()).thenReturn(mockLeads);

    // When
    String response = controller.home();

    // Then
    assertThat(response).contains("2 leads");
    assertThat(response).contains("Spring Boot CRM is running");
  }

  @Test
  void shouldReturnHomeMessageWithZeroLeads() {
    // Given: пустой список
    when(leadService.findAll()).thenReturn(List.of());

    // When
    String response = controller.home();

    // Then
    assertThat(response).contains("0 leads");
  }

  // При необходимости можно добавить тесты для других методов контроллера,
  // например, проверка вызова leadService.findLeads() при показе списка.
}