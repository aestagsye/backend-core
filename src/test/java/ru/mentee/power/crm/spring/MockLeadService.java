package ru.mentee.power.crm.spring;

import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

import java.time.LocalDateTime;
import java.util.*;

public class MockLeadService extends LeadService {
  private final List<Lead> mockLeads;

  public MockLeadService() {
    super(null, null); // repository не используется в mock
    this.mockLeads = List.of(
            new Lead(UUID.randomUUID(), "test1@example.com", "EvilCorp", LeadStatus.QUALIFIED, LocalDateTime.now()),
            new Lead(UUID.randomUUID(), "test2@example.com", "MoreEvilCorp", LeadStatus.QUALIFIED, LocalDateTime.now())
    );
  }

  @Override
  public List<Lead> findAll() {
    return mockLeads;
  }
}