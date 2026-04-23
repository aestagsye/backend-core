package ru.mentee.power.crm.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.CreateDealRequest;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.exception.IllegalLeadStateException;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class LeadService {

  private final LeadRepository leadRepository;
  private final DealRepository dealRepository;
  private final CompanyRepository companyRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(LeadService.class);

  @PostConstruct
  void init() {
    LOGGER.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public Optional<Lead> findByEmail(String email) {
    return leadRepository.findByEmail(email);
  }

  public List<Lead> findByStatuses(LeadStatus... statuses) {
    return leadRepository.findByStatusIn(List.of(statuses));
  }

  public Page<Lead> getFirstPage(int pageSize) {
    PageRequest pageRequest = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
    return leadRepository.findAll(pageRequest);
  }

  public Page<Lead> searchByCompany(String companyName, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return leadRepository.findByCompanyName(companyName, pageable);
  }

  @Transactional
  public int convertNewToContacted() {
    int updated = leadRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
    return updated;
  }

  @Transactional
  public int archiveOldLeads(LeadStatus status) {
    return leadRepository.deleteByStatusBulk(status);
  }

  public List<Lead> findLeads(String search, LeadStatus status) {
    return leadRepository.findAll().stream()
        .filter(
            lead -> {
              if (search == null || search.isBlank()) {
                return true;
              }
              return lead.getEmail().toLowerCase().contains(search.toLowerCase())
                  || lead.getCompany().getName().toLowerCase().contains(search.toLowerCase());
            })
        .filter(
            lead -> {
              if (status == null) {
                return true;
              }
              return lead.getStatus().equals(status);
            })
        .toList();
  }

  @Transactional
  public Lead addLead(String email, Company company, LeadStatus status) {
    Optional<Lead> existing = leadRepository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email " + "already exists: " + email);
    }

    Company resolvedCompany =
        companyRepository
            .findByName(company.getName())
            .orElseGet(() -> companyRepository.save(company));

    Lead lead = new Lead(email, resolvedCompany, status);
    return leadRepository.save(lead);
  }

  public List<Lead> findAll() {
    return leadRepository.findAll();
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return leadRepository.findAll().stream()
        .filter(lead -> lead.getStatus().equals(status))
        .toList();
  }

  public Optional<Lead> findById(UUID id) {
    return leadRepository.findById(id);
  }

  @Transactional
  public Lead update(UUID id, Lead updatedLead) {
    Lead existing =
        leadRepository
            .findById(id)
            .orElseThrow(() -> new IllegalStateException("There is no Lead with such id"));

    existing.setEmail(updatedLead.getEmail());
    existing.setCompany(updatedLead.getCompany());
    existing.setStatus(updatedLead.getStatus());

    return leadRepository.save(existing);
  }

  public void delete(UUID id) {
    if (findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    leadRepository.deleteById(id);
  }

  @Transactional
  public Deal convertLeadToDeal(UUID leadId, CreateDealRequest request) {
    Lead found =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
    if (found.getStatus() != LeadStatus.QUALIFIED) {
      throw new IllegalLeadStateException(leadId, found.getStatus());
    }
    Deal deal = new Deal(leadId, request.getAmount());
    Deal savedDeal = dealRepository.save(deal);

    found.setStatus(LeadStatus.CONVERTED);
    leadRepository.save(found);
    return savedDeal;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processLeads(List<UUID> ids) {
    for (UUID id : ids) {
      this.processSingleLead(id);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processSingleLead(UUID leadId) {
    Lead lead =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
    if (lead.getEmail().contains("throw-exception")) {
      throw new RuntimeException("Simulated error for lead: " + leadId);
    }
    lead.setStatus(LeadStatus.CONTACTED);
    leadRepository.save(lead);
  }
}
