package ru.mentee.power.crm.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Lead {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeadStatus status;

  @Column
  private String company;

  @Version
  @Column(name = "version", nullable = false)
  @Setter(AccessLevel.NONE) // JPA управляет версией сам — НЕ создаём setter
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public Lead(String email, String company, LeadStatus status) {
    this.email = email;
    this.company = company;
    this.status = status;
  }

  public Lead(UUID uuid, String email, String company, LeadStatus status, LocalDateTime createdAt) {
    this.id = uuid;
    this.email = email;
    this.company = company;
    this.status = status;
    this.createdAt = createdAt;
  }
}