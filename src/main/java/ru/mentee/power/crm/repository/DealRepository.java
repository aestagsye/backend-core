package ru.mentee.power.crm.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {
}
