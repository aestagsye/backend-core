package ru.mentee.power.crm.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {
  @EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"})
  @Query("SELECT d FROM Deal d WHERE d.id = :id")
  Optional<Deal> findDealWithProducts(UUID id);

}
