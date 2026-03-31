package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class LeadService {

  private final LeadRepository repository;
  private static final Logger LOGGER = LoggerFactory.getLogger(LeadService.class);

  @PostConstruct
  void init() {
    LOGGER.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  /**
   * Поиск лида по email (derived method).
   */
  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  /**
   * Поиск лидов по списку статусов (JPQL).
   */
  public List<Lead> findByStatuses(LeadStatus... statuses) {
    return repository.findByStatusIn(List.of(statuses));
  }

  /**
   * Получить первую страницу лидов с сортировкой.
   */
  public Page<Lead> getFirstPage(int pageSize) {
    PageRequest pageRequest = PageRequest.of(
            0, // первая страница (нумерация с 0)
            pageSize,
            Sort.by("createdAt").descending()
    );
    return repository.findAll(pageRequest);
  }

  public Page<Lead> searchByCompany(String company, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByCompany(company, pageable);
  }

  /**
   * Массовое обновление статуса (используется @Modifying метод).
   * ВАЖНО: @Transactional обязательна для @Modifying!
   */
  @Transactional
  public int convertNewToContacted() {
    int updated = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    // Логируем для observability
    System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
    return updated;
  }

  @Transactional
  public int archiveOldLeads(LeadStatus status) {
    return repository.deleteByStatusBulk(status);
  }

  public List<Lead> findLeads(String search, LeadStatus status) {
    return repository.findAll().stream()
            .filter(lead -> {
              if (search == null || search.isBlank()) {
                return true;
              }
              return lead.getEmail().toLowerCase().contains(search.toLowerCase())
                      || lead.getCompany().toLowerCase().contains(search.toLowerCase());
            })
            .filter(lead -> {
              if (status == null) {
                return true;
              }
              return lead.getStatus().equals(status);
            })
            .toList();
  }

  public Lead addLead(String email, String company, LeadStatus status) {
    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email "
              + "already exists: " + email);
    }

    Lead lead = new Lead(null, email,
            company, status, null);
    return repository.save(lead);
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return repository.findAll().stream()
            .filter(lead -> lead.getStatus().equals(status))
            .toList();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
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
    repository.deleteById(id);
  }
}