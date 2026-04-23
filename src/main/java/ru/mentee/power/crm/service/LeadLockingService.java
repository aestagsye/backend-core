package ru.mentee.power.crm.service;

import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadLockingService {

  private final LeadRepository leadRepository;

  public LeadLockingService(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional
  public Lead convertLeadToDealWithLock(UUID leadId, LeadStatus newStatus) {
    Lead lead =
        leadRepository
            .findByIdForUpdate(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    lead.setStatus(newStatus);
    return leadRepository.save(lead);
  }

  @Transactional
  public Lead updateLeadStatusOptimistic(UUID leadId, LeadStatus newStatus) {
    Lead lead =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    lead.setStatus(newStatus);
    return leadRepository.save(lead);
  }

  @Transactional
  public Lead updateWithRetry(UUID leadId, LeadStatus newStatus) {
    try {
      return updateLeadStatusOptimistic(leadId, newStatus);
    } catch (OptimisticLockException e) {
      throw new RuntimeException("attempts due to optimistic lock conflicts", e);
    }
  }

  @Transactional
  public void processLeadsInOrder(List<UUID> ids) {
    for (UUID id : ids) {
      Lead lead =
          leadRepository
              .findByIdForUpdate(id)
              .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + id));
      lead.setStatus(LeadStatus.CONTACTED);
      leadRepository.save(lead);
    }
  }
}
