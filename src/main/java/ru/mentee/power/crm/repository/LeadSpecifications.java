package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

public final class LeadSpecifications {

  private LeadSpecifications() {}

  public static Specification<Lead> hasStatus(LeadStatus status) {
    return (root, query, builder) -> builder.equal(root.get("status"), status);
  }

  public static Specification<Lead> emailContainsIgnoreCase(String search) {
    String normalizedSearch = "%" + search.toLowerCase() + "%";
    return (root, query, builder) ->
        builder.like(builder.lower(root.get("email")), normalizedSearch);
  }

  public static Specification<Lead> companyNameContainsIgnoreCase(String search) {
    String normalizedSearch = "%" + search.toLowerCase() + "%";
    return (root, query, builder) ->
        builder.like(builder.lower(root.get("company").get("name")), normalizedSearch);
  }

  public static Specification<Lead> emailOrCompanyNameContainsIgnoreCase(String search) {
    return Specification.where(emailContainsIgnoreCase(search))
        .or(companyNameContainsIgnoreCase(search));
  }
}
