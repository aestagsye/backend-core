package ru.mentee.power.crm.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import ru.mentee.power.crm.domain.LeadStatus;

public record LeadResponse(
    UUID id, String email, LeadStatus status, String companyName, LocalDateTime createdAt) {}
