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
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.dto.LeadResponse;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@RequestMapping("/api/leads")
@RestController
@RequiredArgsConstructor
public class LeadRestController {
  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    List<LeadResponse> responses =
        leadService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(@PathVariable UUID id) {
    return leadService
        .findById(id)
        .map(leadMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody LeadFormDto formDto) {
    Lead toCreate = leadMapper.toEntity(formDto);
    Lead created =
        leadService.addLead(toCreate.getEmail(), toCreate.getCompany(), toCreate.getStatus());
    URI location = URI.create("/api/leads/" + created.getId());
    return ResponseEntity.created(location).body(leadMapper.toResponse(created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(
      @PathVariable UUID id, @Valid @RequestBody LeadFormDto formDto) {
    Lead updatedLead = leadMapper.toEntity(formDto);
    Optional<Lead> updated = leadService.updateLead(id, updatedLead);
    return updated
        .map(leadMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
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
}
