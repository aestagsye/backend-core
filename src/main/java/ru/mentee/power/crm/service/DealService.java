package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class DealService {
  private final DealRepository dealRepository;
  private final LeadRepository leadRepository;

  @Transactional
  public Deal transitionDealStatus(UUID dealId, DealStatus newStatus) {
    Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new IllegalArgumentException("Deal not found: " + dealId));
    deal.transitionTo(newStatus);
    dealRepository.save(deal);
    return deal;
  }

  public List<Deal> getAllDeals() {
    return dealRepository.findAll();
  }

  public Map<DealStatus, List<Deal>> getDealsByStatusForKanban() {
    return dealRepository.findAll().stream()
            .collect(Collectors.groupingBy(Deal::getStatus));
  }
}