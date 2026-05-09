package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.dto.LeadFormDto;
import ru.mentee.power.crm.dto.LeadResponse;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.generated.LeadManagementApi;

@RestController
@RequiredArgsConstructor
public class LeadRestController implements LeadManagementApi {

  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @Override
  public ResponseEntity<List<LeadResponse>> getLeads() {
    List<LeadResponse> responses =
        leadService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @Override
  public ResponseEntity<LeadResponse> createLead(@Valid LeadFormDto dto) {
    var lead = leadMapper.toEntity(dto);
    var created = leadService.addLead(lead.getEmail(), lead.getCompany(), lead.getStatus());
    URI location = URI.create("/api/leads/" + created.getId());
    return ResponseEntity.created(location).body(leadMapper.toResponse(created));
  }

  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    LeadResponse response = leadService.getLeadById(id);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, @Valid LeadFormDto dto) {
    LeadResponse response = leadService.updateLeadRest(id, dto);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    leadService.deleteLeadRest(id);
    return ResponseEntity.noContent().build();
  }
}
