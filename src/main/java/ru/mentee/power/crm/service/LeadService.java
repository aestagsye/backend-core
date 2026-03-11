package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LeadService.class);
  private final LeadRepository repository;

  public LeadService(LeadRepository repository) {
    this.repository = repository;
    LOGGER.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOGGER.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public Lead addLead(String email, String company, LeadStatus status) {
    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(
            UUID.randomUUID(),
            email,
            company,
            status
    );

    return repository.save(lead);
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return repository.findAll().stream()
            .filter(lead -> lead.status().equals(status))
            .toList();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  public Lead update(UUID id, Lead updatedLead) {
    if (repository.findById(id).isEmpty()) {
      throw new IllegalStateException("There is no Lead with such id");
    }
    repository.save(updatedLead);
    return updatedLead;
  }

  public void delete(UUID id) {
    if (findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    repository.delete(id);
  }

  public List<Lead> findLeads(String search, LeadStatus status) {
    return repository.findAll().stream()
            .filter(lead -> {
              if (search == null || search.isBlank()) return true;
              return lead.email().toLowerCase().contains(search.toLowerCase())
                      || lead.company().toLowerCase().contains(search.toLowerCase());
            })
            .filter(lead -> {
              if (status == null) return true;
              return lead.status().equals(status);
            })
            .toList();
  }
}