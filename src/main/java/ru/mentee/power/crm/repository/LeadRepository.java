package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.domain.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
  Optional<Lead> findByEmail(String email);

  @Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
  Optional<Lead> findByEmailNative(String email);

  @Query(value = "SELECT * FROM leads WHERE status = ?1", nativeQuery = true)
  List<Lead> findByStatusNative(String status);
}