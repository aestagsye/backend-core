package ru.mentee.power.crm.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import ru.mentee.power.crm.entity.DealProduct;

@Entity
@Table(name = "deals")
public class Deal {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID leadId;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DealStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DealProduct> dealProducts = new ArrayList<>();

  public Deal() {
  }

  public Deal(UUID leadId, BigDecimal amount) {
    this.leadId = Objects.requireNonNull(leadId, "leadId must not be null");
    this.amount = Objects.requireNonNull(amount, "amount must not be null");
    this.status = DealStatus.NEW;
  }

  public Deal(UUID id, UUID leadId, BigDecimal amount, DealStatus status, LocalDateTime createdAt) {
    this.leadId = leadId;
    this.amount = amount;
    this.status = status;
  }

  public void transitionTo(DealStatus newStatus) {
    if (!status.canTransitionTo(newStatus)) {
      throw new IllegalStateException("Cannot transition from " + status + " to " + newStatus);
    }
    this.status = newStatus;
  }

  public void addDealProduct(DealProduct dealProduct) {
    dealProducts.add(dealProduct);
    dealProduct.setDeal(this);
  }

  public void removeDealProduct(DealProduct dealProduct) {
    dealProducts.remove(dealProduct);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getLeadId() {
    return leadId;
  }

  public void setLeadId(UUID leadId) {
    this.leadId = leadId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public DealStatus getStatus() {
    return status;
  }

  public void setStatus(DealStatus status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<DealProduct> getDealProducts() {
    return dealProducts;
  }

  public void setDealProducts(List<DealProduct> dealProducts) {
    this.dealProducts = dealProducts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Deal deal = (Deal) o;
    return Objects.equals(id, deal.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}