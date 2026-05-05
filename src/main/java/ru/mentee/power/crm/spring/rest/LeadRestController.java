package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.service.LeadService;

@RequestMapping("/api/leads")
@RestController
@RequiredArgsConstructor
public class LeadRestController {
  private final LeadService leadService;

  @GetMapping
  public List<Lead> getAllLeads() {
    return leadService.findAll();
  }

  @GetMapping("/{id}")
  public Lead getLeadById(@PathVariable UUID id) {
    return leadService
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Lead createLead(@Valid @RequestBody LeadFormDto formDto) {
    return leadService.addLead(formDto.getEmail(), createCompany(formDto), formDto.getStatus());
  }

  @PutMapping("/{id}")
  public Lead updateLead(@PathVariable UUID id, @Valid @RequestBody LeadFormDto formDto) {
    Lead lead = new Lead(formDto.getEmail(), createCompany(formDto), formDto.getStatus());
    return leadService.update(id, lead);
  }

  private Company createCompany(LeadFormDto formDto) {
    return new Company(formDto.getCompanyName(), formDto.getIndustry());
  }
}
