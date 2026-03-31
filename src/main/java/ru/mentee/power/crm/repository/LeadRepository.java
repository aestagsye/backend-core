package ru.mentee.power.crm.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
  // Derived query methods (Spring генерирует SQL автоматически)

  /**
   * Поиск лида по email (точное совпадение).
   * SQL: SELECT * FROM leads WHERE email = ?
   */
  Optional<Lead> findByEmail(String email);

  /**
   * Поиск лидов по статусу.
   * SQL: SELECT * FROM leads WHERE status = ?
   */
  List<Lead> findByStatus(LeadStatus status);

  List<Lead> findByCompany(String company);

  long countByStatus(LeadStatus status);

  boolean existsByEmail(String email);

  /**
   * Поиск лидов по части email (LIKE запрос).
   * SQL: SELECT * FROM leads WHERE email LIKE '%emailPart%'
   */
  List<Lead> findByEmailContaining(String emailPart);

  List<Lead> findByStatusAndCompany(LeadStatus status, String company);

  List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status);

  // JPQL запросы (объектный язык)

  /**
   * Поиск лидов по списку статусов (JPQL).
   * JPQL: SELECT l FROM Lead l WHERE l.status IN :statuses
   * SQL: SELECT * FROM leads WHERE status IN (?, ?, ...)
   */
  @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
  List<Lead> findByStatusIn(@Param("statuses") List<LeadStatus> statuses);

  @Query("SELECT l FROM Lead l WHERE l.createdAt > :date")
  List<Lead> findCreatedAfter(@Param("date") LocalDateTime date);

  /**
   * Поиск лидов с фильтрацией и сортировкой (JPQL).
   */
  @Query("SELECT l FROM Lead l WHERE l.company = :company ORDER BY l.createdAt DESC")
  List<Lead> findByCompanyOrderedByDate(@Param("company") String company);

  @Query("SELECT l FROM Lead l WHERE l.company = :companyName")
  List<Lead> findByCompanyName(@Param("companyName") String companyName);
  // Методы с пагинацией

  /**
   * Поиск всех лидов с пагинацией (переопределяем из JpaRepository).
   * Клиент: PageRequest.of(0, 20) — первая страница, 20 элементов
   */
  Page<Lead> findAll(Pageable pageable);

  /**
   * Поиск по статусу с пагинацией (derived method).
   */
  Page<Lead> findByStatus(LeadStatus status, Pageable pageable);

  Page<Lead> findByCompany(String company, Pageable pageable);

  /**
   * JPQL запрос с пагинацией.
   */
  @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
  Page<Lead> findByStatusInPaged(@Param("statuses") List<LeadStatus> statuses, Pageable pageable);

  // Bulk операции

  /**
   * Массовое обновление статуса лидов.
   * ВАЖНО: требует @Transactional на уровне Service!
   *
   * @return количество обновлённых строк
   */
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Lead l SET l.status = :newStatus WHERE l.status = :oldStatus")
  int updateStatusBulk(
          @Param("oldStatus") LeadStatus oldStatus,
          @Param("newStatus") LeadStatus newStatus
  );

  @Modifying
  @Query("DELETE FROM Lead l WHERE l.status = :status")
  int deleteByStatusBulk(@Param("status") LeadStatus status);
}