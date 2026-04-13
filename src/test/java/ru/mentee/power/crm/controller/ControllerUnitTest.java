package ru.mentee.power.crm.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.ConcurrentModel;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.CreateDealRequest;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.controller.DealController;
import ru.mentee.power.crm.spring.controller.LeadController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ControllerUnitTest {

  @Autowired
  private LeadController leadController;

  @Autowired
  private DealController dealController;

  @MockitoBean
  private LeadService leadService;

  @MockitoBean
  private DealService dealService;

  @Test
  void shouldReturnHomeMessage() {
    String result = leadController.home();

    assertThat(result).contains("Spring Boot CRM is running");
  }

  @Test
  void shouldShowLeadsList() {
    when(leadService.findLeads(null, null)).thenReturn(java.util.List.of());

    ConcurrentModel model = new ConcurrentModel();
    String result = leadController.showLeads(null, null, model);

    assertThat(result).isEqualTo("leads/list");
    assertThat(model.getAttribute("leads")).isNotNull();
  }

  @Test
  void shouldShowLeadsWithFilters() {
    when(leadService.findLeads("test", LeadStatus.NEW)).thenReturn(java.util.List.of());

    ConcurrentModel model = new ConcurrentModel();
    String result = leadController.showLeads("test", LeadStatus.NEW, model);

    assertThat(result).isEqualTo("leads/list");
    assertThat(model.getAttribute("currentFilter")).isEqualTo(LeadStatus.NEW);
    assertThat(model.getAttribute("search")).isEqualTo("test");
  }

  @Test
  void shouldShowCreateForm() {
    ConcurrentModel model = new ConcurrentModel();
    String result = leadController.showCreateForm(model);

    assertThat(result).isEqualTo("leads/create");
    assertThat(model.getAttribute("formDto")).isNotNull();
  }

  @Test
  void shouldShowEditForm() {
    UUID id = UUID.randomUUID();
    Company company = new Company("Test", "Tech");
    Lead lead = new Lead(id, "test@test.com", company, LeadStatus.NEW, LocalDateTime.now());
    when(leadService.findById(id)).thenReturn(Optional.of(lead));

    ConcurrentModel model = new ConcurrentModel();
    String result = leadController.showEditForm(id, model);

    assertThat(result).isEqualTo("spring/edit");
    assertThat(model.getAttribute("lead")).isEqualTo(lead);
  }

  @Test
  void shouldThrowException_whenEditNotFound() {
    UUID id = UUID.randomUUID();
    when(leadService.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> leadController.showEditForm(id, new ConcurrentModel()))
            .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
  }

  @Test
  void shouldDeleteLead() {
    UUID id = UUID.randomUUID();
    doNothing().when(leadService).delete(id);

    String result = leadController.deleteLead(id);

    assertThat(result).isEqualTo("redirect:/leads");
    verify(leadService).delete(id);
  }

  @Test
  void shouldListDeals() {
    when(dealService.getAllDeals()).thenReturn(java.util.List.of());

    ConcurrentModel model = new ConcurrentModel();
    String result = dealController.listDeals(model);

    assertThat(result).isEqualTo("deals/list");
    assertThat(model.getAttribute("deals")).isNotNull();
  }

  @Test
  void shouldShowKanban() {
    when(dealService.getDealsByStatusForKanban()).thenReturn(Map.of());

    ConcurrentModel model = new ConcurrentModel();
    String result = dealController.kanbanView(model);

    assertThat(result).isEqualTo("deals/kanban");
    assertThat(model.getAttribute("dealsByStatus")).isNotNull();
  }

  @Test
  void shouldShowConvertForm() {
    UUID leadId = UUID.randomUUID();
    Company company = new Company("Test", "Tech");
    Lead lead = new Lead(leadId, "test@test.com", company, LeadStatus.QUALIFIED, LocalDateTime.now());
    when(leadService.findById(leadId)).thenReturn(Optional.of(lead));

    ConcurrentModel model = new ConcurrentModel();
    String result = dealController.showConvertForm(leadId, model);

    assertThat(result).isEqualTo("deals/convert");
    assertThat(model.getAttribute("lead")).isEqualTo(lead);
  }

  @Test
  void shouldThrowException_whenConvertFormNotFound() {
    UUID leadId = UUID.randomUUID();
    when(leadService.findById(leadId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> dealController.showConvertForm(leadId, new ConcurrentModel()))
            .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
  }

  @Test
  void shouldConvertLeadToDeal() {
    UUID leadId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();
    Company company = new Company("Test", "Tech");
    company.setId(companyId);
    Lead lead = new Lead(leadId, "test@test.com", company, LeadStatus.QUALIFIED, LocalDateTime.now());
    when(leadService.findById(leadId)).thenReturn(Optional.of(lead));

    String result = dealController.convertLeadToDeal(leadId, BigDecimal.valueOf(10000));

    assertThat(result).isEqualTo("redirect:/deals");
    verify(leadService).convertLeadToDeal(eq(leadId), any(CreateDealRequest.class));
  }

  @Test
  void shouldTransitionDealStatus() {
    UUID dealId = UUID.randomUUID();
    Deal deal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10000));
    when(dealService.transitionDealStatus(eq(dealId), any())).thenReturn(deal);

    String result = dealController.transitionStatus(dealId, DealStatus.QUALIFIED);

    assertThat(result).isEqualTo("redirect:/deals/kanban");
    verify(dealService).transitionDealStatus(dealId, DealStatus.QUALIFIED);
  }
}
