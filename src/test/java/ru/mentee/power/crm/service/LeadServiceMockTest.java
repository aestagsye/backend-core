package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@ExtendWith(MockitoExtension.class)
class LeadServiceMockTest {

  @Mock private LeadRepository mockRepository;

  @Mock private DealRepository mockRepository1;

  @Mock private CompanyRepository mockCompanyRepository;

  private LeadService service;

  @BeforeEach
  void setUp() {
    service = new LeadService(mockRepository, mockRepository1, mockCompanyRepository);
  }

  @Test
  void shouldCallRepositorySave_whenAddingNewLead() {
    // Given:
    when(mockRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(mockCompanyRepository.findByName(anyString())).thenReturn(Optional.empty());
    when(mockCompanyRepository.save(any(Company.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When:
    when(mockRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When:
    Lead result =
        service.addLead("new@example.com", new Company("Company", "Industry"), LeadStatus.NEW);

    // Then:
    verify(mockRepository, times(1)).save(any(Lead.class));

    // Then:
    assertThat(result.getEmail()).isEqualTo("new@example.com");
  }

  @Test
  void shouldNotCallSave_whenEmailExists() {
    // Given:
    Lead existingLead =
        new Lead(
            UUID.randomUUID(),
            "existing@example.com",
            new Company("Existing Company", "Existing Industry"),
            LeadStatus.CONTACTED,
            LocalDateTime.now());
    when(mockRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingLead));

    // When/Then:
    assertThatThrownBy(
            () ->
                service.addLead(
                    "existing@example.com",
                    new Company("New Company", "New Industry"),
                    LeadStatus.NEW))
        .isInstanceOf(IllegalStateException.class);

    // Then:
    verify(mockRepository, never()).save(any(Lead.class));
  }

  @Test
  void shouldCallFindByEmail_beforeSave() {
    // Given
    when(mockRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(mockCompanyRepository.findByName(anyString())).thenReturn(Optional.empty());
    when(mockCompanyRepository.save(any(Company.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(mockRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    service.addLead("test@example.com", new Company("Company", "Industry"), LeadStatus.NEW);

    // Then:
    var inOrder = inOrder(mockRepository);
    inOrder.verify(mockRepository).findByEmail("test@example.com");
    inOrder.verify(mockRepository).save(any(Lead.class));
  }
}
