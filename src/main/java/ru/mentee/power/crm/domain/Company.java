package ru.mentee.power.crm.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  private String industry;

  @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
  @ToString.Exclude
  private List<Lead> leads = new ArrayList<>();

  public Company(String name, String industry) {
    this.name = name;
    this.industry = industry;
  }

  public void  addLead(Lead lead) {
    leads.add(lead);
    lead.setCompany(this);
  }

  public void removeLead(Lead lead) {
    leads.remove(lead);
    lead.setCompany(null);
  }
}
