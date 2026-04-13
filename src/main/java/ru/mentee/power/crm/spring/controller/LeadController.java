package ru.mentee.power.crm.spring.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequiredArgsConstructor
public class LeadController {
  private final LeadService leadService;
  String redirectLeads = "redirect:/leads";

  @GetMapping("/")
  @ResponseBody
  public String home() {
    return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
  }

  @GetMapping("/leads")
  public String showLeads(
          @RequestParam(required = false) String search,
          @RequestParam(required = false) LeadStatus status,
          Model model
  ) {
    List<Lead> leads = leadService.findLeads(search, status);
    model.addAttribute("leads", leads);
    model.addAttribute("currentFilter", status);
    model.addAttribute("search", search != null ? search : "");
    return "leads/list";
  }

  @GetMapping("/leads/new")
  public String showCreateForm(Model model) {
    LeadFormDto formDto = new LeadFormDto();
    formDto.setStatus(LeadStatus.NEW);
    model.addAttribute("formDto", formDto);
    return "leads/create";
  }

  @GetMapping("/leads/{id}/edit")
  public String showEditForm(@PathVariable UUID id, Model model) {
    Lead lead = leadService.findById(id).orElseThrow(()
            -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Lead not found"));
    model.addAttribute("lead", lead);
    return "spring/edit";
  }

  @PostMapping("/leads/{id}")
  public String update(@PathVariable UUID id,
                       @Valid @ModelAttribute LeadFormDto formDto,
                       BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("formAction", "/leads/" + id);
      model.addAttribute("submitButtonText", "Редактировать");
      Company company = null;
      if (formDto.getCompanyName() != null || formDto.getIndustry() != null) {
        company = new Company(formDto.getCompanyName(), formDto.getIndustry());
      }
      Lead lead = new Lead(formDto.getEmail(), company, formDto.getStatus());
      model.addAttribute("lead", lead);
      return "spring/edit";
    }
    Lead existing = leadService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    existing.setEmail(formDto.getEmail());
    if (existing.getCompany() != null) {
      existing.getCompany().setName(formDto.getCompanyName());
      existing.getCompany().setIndustry(formDto.getIndustry());
    } else {
      Company company = new Company(formDto.getCompanyName(), formDto.getIndustry());
      existing.setCompany(company);
    }
    existing.setStatus(formDto.getStatus());
    leadService.update(id, existing);
    return redirectLeads;
  }

  @PostMapping("/leads")
  public String createLead(@Valid @ModelAttribute LeadFormDto formDto,
                           BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("formAction", "/leads");
      model.addAttribute("submitButtonText", "Создать");
      Company company = null;
      if (formDto.getCompanyName() != null || formDto.getIndustry() != null) {
        company = new Company(formDto.getCompanyName(), formDto.getIndustry());
      }
      Lead lead = new Lead(formDto.getEmail(), company, formDto.getStatus());
      model.addAttribute("lead", lead);
      return "leads/form";
    }
    Company company = new Company(formDto.getCompanyName(), formDto.getIndustry());
    leadService.addLead(formDto.getEmail(), company, formDto.getStatus());
    return redirectLeads;
  }

  @PostMapping("/leads/{id}/delete")
  public String deleteLead(@PathVariable UUID id) {
    leadService.delete(id);
    return redirectLeads;
  }
}