package ru.mentee.power.crm.model;

import java.util.UUID;

public record Lead(
        UUID id,
        String email,
        String company,
        LeadStatus status
) {
  // Record автоматически генерирует equals/hashCode по всем полям
  // HashMap сможет использовать id как ключ
}