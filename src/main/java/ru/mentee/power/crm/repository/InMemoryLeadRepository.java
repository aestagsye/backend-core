package ru.mentee.power.crm.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import ru.mentee.power.crm.domain.Lead;

public class InMemoryLeadRepository implements LeadRepository {
  private final Map<UUID, Lead> storage = new HashMap<>();
  private final Map<String, UUID> emailIndex = new HashMap<>();

  @Override
  public Lead save(Lead lead) {
    UUID id = lead.getId();

    if (id == null) {
      id = UUID.randomUUID();
      // Create new Lead instance with generated id (assuming immutable Lead)
      lead = new Lead(
              id,
              lead.getEmail(),
              lead.getCompany(),
              lead.getStatus(),
              lead.getCreatedAt()
      );
    }
    if (findById(lead.getId()).isPresent()) {
      deleteById(lead.getId());
    }
    storage.put(lead.getId(), lead);
    emailIndex.put(lead.getEmail(), lead.getId());
    return lead;
  }

  @Override
  public Optional<Lead> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public boolean existsById(UUID uuid) {
    return false;
  }

  @Override
  public Optional<Lead> findByEmail(String email) {
    UUID id = emailIndex.get(email);
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public <S extends Lead> List<S> saveAll(Iterable<S> entities) {
    return List.of();
  }

  @Override
  public List<Lead> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public List<Lead> findAllById(Iterable<UUID> uuids) {
    return List.of();
  }

  @Override
  public long count() {
    return storage.size();
  }

  @Override
  public void deleteById(UUID uuid) {
    Lead lead = storage.remove(uuid);
    if (lead != null) {
      emailIndex.remove(lead.getEmail());
    }
  }

  @Override
  public void delete(Lead entity) {
  }

  @Override
  public void deleteAllById(Iterable<? extends UUID> uuids) {

  }

  @Override
  public void deleteAll(Iterable<? extends Lead> entities) {

  }

  @Override
  public void deleteAll() {

  }

  @Override
  public Optional<Lead> findByEmailNative(String email) {
    return Optional.empty();
  }

  @Override
  public List<Lead> findByStatusNative(String status) {
    return List.of();
  }

  @Override
  public void flush() {

  }

  @Override
  public <S extends Lead> S saveAndFlush(S entity) {
    return null;
  }

  @Override
  public <S extends Lead> List<S> saveAllAndFlush(Iterable<S> entities) {
    return List.of();
  }

  @Override
  public void deleteAllInBatch(Iterable<Lead> entities) {

  }

  @Override
  public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

  }

  @Override
  public void deleteAllInBatch() {

  }

  @Override
  public Lead getOne(UUID uuid) {
    return null;
  }

  @Override
  public Lead getById(UUID uuid) {
    return null;
  }

  @Override
  public Lead getReferenceById(UUID uuid) {
    return null;
  }

  @Override
  public <S extends Lead> Optional<S> findOne(Example<S> example) {
    return Optional.empty();
  }

  @Override
  public <S extends Lead> List<S> findAll(Example<S> example) {
    return List.of();
  }

  @Override
  public <S extends Lead> List<S> findAll(Example<S> example, Sort sort) {
    return List.of();
  }

  @Override
  public <S extends Lead> Page<S> findAll(Example<S> example, Pageable pageable) {
    return null;
  }

  @Override
  public <S extends Lead> long count(Example<S> example) {
    return 0;
  }

  @Override
  public <S extends Lead> boolean exists(Example<S> example) {
    return false;
  }

  @Override
  public <S extends Lead, R> R findBy(Example<S> example,
                                      Function<FluentQuery.FetchableFluentQuery<S>,
                                              R> queryFunction) {
    return null;
  }

  @Override
  public List<Lead> findAll(Sort sort) {
    return List.of();
  }

  @Override
  public Page<Lead> findAll(Pageable pageable) {
    return null;
  }
}
