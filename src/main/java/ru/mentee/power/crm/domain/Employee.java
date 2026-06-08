package ru.mentee.power.crm.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "employees")
@Setter
@Getter
@Entity
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column
  private String name;

  @Column
  private BigDecimal salary;


}
