package ru.mentee.power.crm.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadListServletTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private ServletContext servletContext;

  @Mock
  private ServletConfig servletConfig;

  @Mock
  private LeadService leadService;

  private LeadListServlet servlet;
  private StringWriter stringWriter;
  private PrintWriter printWriter;

  @BeforeEach
  void setUp() throws Exception {
    servlet = new LeadListServlet();

    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    servlet.init(servletConfig);
  }

  @Test
  void doGet_shouldReturnHtmlTableWithLeads() throws Exception {
    when(response.getWriter()).thenReturn(printWriter);
    when(servletContext.getAttribute("leadService")).thenReturn(leadService);

    List<Lead> mockLeads = Arrays.asList(
            new Lead(UUID.randomUUID(), "test1@example.com", "Company A", LeadStatus.NEW),
            new Lead(UUID.randomUUID(), "test2@example.com", "Company B", LeadStatus.CONTACTED)
    );

    when(leadService.findAll()).thenReturn(mockLeads);

    servlet.doGet(request, response);

    printWriter.flush();
    String result = stringWriter.toString();

    verify(response).setContentType("text/html; charset=UTF-8");
    assertTrue(result.contains("test1@example.com"));
    assertTrue(result.contains("Company A"));
  }

  @Test
  void doGet_whenServiceNotFound_shouldThrowException() throws Exception {
    when(servletContext.getAttribute("leadService")).thenReturn(null);

    assertThrows(NullPointerException.class, () -> {
      servlet.doGet(request, response);
    });
  }
}