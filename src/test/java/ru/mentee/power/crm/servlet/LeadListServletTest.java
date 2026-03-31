package ru.mentee.power.crm.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.LeadServiceLegacy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadListServletTest {

  @Mock
  private ServletConfig servletConfig;

  @Mock
  private ServletContext servletContext;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private LeadServiceLegacy leadService;

  private LeadListServlet servlet;
  private StringWriter responseWriter;
  private PrintWriter printWriter;

  @BeforeEach
  void setUp() throws Exception {
    servlet = new LeadListServlet();

    when(servletConfig.getServletContext()).thenReturn(servletContext);

    servlet.init(servletConfig);

    responseWriter = new StringWriter();
    printWriter = new PrintWriter(responseWriter);
  }

  @Test
  void testDoGetRendersLeadsCorrectly() throws Exception {
    List<Lead> mockLeads = List.of(
            new Lead(UUID.randomUUID(), "lead1@test.com", "Company1", LeadStatus.NEW, LocalDateTime.now()),
            new Lead(UUID.randomUUID(), "lead2@test.com", "Company2", LeadStatus.CONTACTED, LocalDateTime.now())
    );

    when(servletContext.getAttribute("leadService")).thenReturn(leadService);
    when(leadService.findAll()).thenReturn(mockLeads);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);
    printWriter.flush();
    String result = responseWriter.toString();

    verify(response).setContentType("text/html; charset=UTF-8");
    assertThat(result)
            .contains("lead1@test.com")
            .contains("lead2@test.com")
            .contains("Company1")
            .contains("Company2")
            .contains("NEW")
            .contains("CONTACTED");
  }

  @Test
  void testDoGetWithEmptyLeadsList() throws Exception {
    List<Lead> emptyLeads = List.of();

    when(servletContext.getAttribute("leadService")).thenReturn(leadService);
    when(leadService.findAll()).thenReturn(emptyLeads);
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);
    printWriter.flush();
    String result = responseWriter.toString();

    assertThat(result)
            .contains("Lead List")
            .contains("<table") // Таблица должна присутствовать
            .doesNotContain("test@example.com"); // Но данных не должно быть
  }

  @Test
  void testDoGetSetsCorrectContentType() throws Exception {
    when(servletContext.getAttribute("leadService")).thenReturn(leadService);
    when(leadService.findAll()).thenReturn(List.of());
    when(response.getWriter()).thenReturn(printWriter);

    servlet.doGet(request, response);

    verify(response).setContentType("text/html; charset=UTF-8");
  }

}