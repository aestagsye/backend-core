package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

public class LeadService {

  private final LeadRepository repository;

  // DI через конструктор — не создаём repository внутри!
  public LeadService(LeadRepository repository) {
    this.repository = repository;
  }

  public Lead addLead(String email, String company, LeadStatus status) {
    // Бизнес-правило: проверка уникальности email
    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    // Создаём нового лида
    Lead lead = new Lead(
            UUID.randomUUID(),
            email,
            company,
            status
    );

    // Сохраняем через repository
    return repository.save(lead);
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }
}