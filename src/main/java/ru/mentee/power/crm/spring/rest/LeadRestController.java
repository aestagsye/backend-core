package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
  public ResponseEntity<List<Lead>> getAllLeads() {
    List<Lead> leads = leadService.findAll();
    return ResponseEntity.ok(leads); // stub
  }

  @GetMapping("/{id}")
  public ResponseEntity<Lead> getLeadById(@PathVariable UUID id) {
    return leadService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Lead> createLead(@Valid @RequestBody LeadFormDto formDto) {
    Lead created =
        leadService.addLead(formDto.getEmail(), createCompany(formDto), formDto.getStatus());
    URI location = URI.create("/api/leads/" + created.getId());
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Lead> updateLead(
      @PathVariable UUID id, @Valid @RequestBody LeadFormDto formDto) {
    Lead lead = new Lead(formDto.getEmail(), createCompany(formDto), formDto.getStatus());
    Optional<Lead> updated = leadService.updateLead(id, lead);
    return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    boolean deleted = leadService.deleteLead(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private Company createCompany(LeadFormDto formDto) {
    return new Company(formDto.getCompanyName(), formDto.getIndustry());
  }
}
