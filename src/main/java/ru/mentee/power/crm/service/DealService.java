package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class DealService {
  private final DealRepository dealRepository;
  private final LeadRepository leadRepository;

  public DealService(DealRepository dealRepository, LeadRepository leadRepository) {
    this.dealRepository = dealRepository;
    this.leadRepository = leadRepository;
  }

  public Deal convertLeadToDeal(UUID leadId, BigDecimal amount) {
    if (leadRepository.findById(leadId).isEmpty()) {
      throw new IllegalArgumentException("Lead not found: " + leadId);
    }
    Deal deal = new Deal(leadId, amount);
    dealRepository.save(deal);
    return deal;
  }

  public Deal transitionDealStatus(UUID dealId, DealStatus newStatus) {
    Optional<Deal> deal = dealRepository.findById(dealId);
    if (deal.isEmpty()) {
      throw new IllegalArgumentException("Deal not found: " + dealId);
    }
    deal.get().transitionTo(newStatus);
    return deal.get();
  }

  public List<Deal> getAllDeals() {
    return dealRepository.findAll();
  }

  public Map<DealStatus, List<Deal>> getDealsByStatusForKanban() {
    return dealRepository.findAll().stream()
            .collect(Collectors.groupingBy(Deal::getStatus));
  }
}