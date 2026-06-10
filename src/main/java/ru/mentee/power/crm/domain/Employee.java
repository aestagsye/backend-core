package ru.mentee.power.crm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Table(name = "employees")
@Setter
@Getter
@Entity
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal salary;

  protected Employee() {}

  public Employee(String name, BigDecimal salary) {
    this.name = name;
    this.salary = salary;
  }
}
