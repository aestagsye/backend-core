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
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequiredArgsConstructor
public class LeadController {
  private final LeadService leadService;

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
    model.addAttribute("lead", new Lead(null, "", "", LeadStatus.NEW));
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
  public String update(@PathVariable UUID id, @Valid @ModelAttribute Lead lead, BindingResult result,
                       Model model) {
    if (result.hasErrors()) {
      model.addAttribute("formAction", "/leads/" + id);
      model.addAttribute("submitButtonText", "Редактировать");
      model.addAttribute("errors", result);
      return "leads/form";
    }
    leadService.update(id, lead);
    return "redirect:/leads";
  }

  @PostMapping("/leads")
  public String createLead(@Valid @ModelAttribute Lead lead, BindingResult result,
                           Model model) {
    if (result.hasErrors()) {
      model.addAttribute("formAction", "/leads");
      model.addAttribute("submitButtonText", "Создать");
      model.addAttribute("errors", result);
      return "leads/form";
    }
    leadService.addLead(lead.email(), lead.company(), lead.status());
    return "redirect:/leads";
  }

  @PostMapping("/leads/{id}/delete")
  public String deleteLead(@PathVariable UUID id) {
    leadService.delete(id);
    return "redirect:/leads";
  }
}