package ru.mentee.power.crm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.repository.CompanyRepository;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;

  @Transactional
  public Company resolveCompany(Company company) {
    return companyRepository
        .findByName(company.getName())
        .orElseGet(() -> companyRepository.save(company));
  }

  @Transactional
  public Company resolveCompany(String name, String industry) {
    return resolveCompany(new Company(name, industry));
  }
}
