package ru.mentee.power.crm.spring;

import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import java.util.*;

public class MockLeadService extends LeadService {
  private final List<Lead> mockLeads;

  public MockLeadService() {
    super(null); // repository не используется в mock
    this.mockLeads = List.of(
            new Lead(UUID.randomUUID(), "test1@example.com", "EvilCorp", LeadStatus.QUALIFIED),
            new Lead(UUID.randomUUID(), "test2@example.com", "MoreEvilCorp", LeadStatus.QUALIFIED)
    );
  }

  @Override
  public List<Lead> findAll() {
    return mockLeads;
  }
}