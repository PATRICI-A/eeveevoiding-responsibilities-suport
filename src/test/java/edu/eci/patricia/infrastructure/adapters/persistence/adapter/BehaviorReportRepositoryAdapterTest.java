package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.model.ReportType;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.BehaviorReportEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.BehaviorReportMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.BehaviorReportJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BehaviorReportRepositoryAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class BehaviorReportRepositoryAdapterTest {

    @Mock
    private BehaviorReportJpaRepository jpaRepository;

    @Mock
    private BehaviorReportMapper mapper;

    @InjectMocks
    private BehaviorReportRepositoryAdapter adapter;

    private UUID reportId;
    private UUID reporterId;
    private BehaviorReport domainReport;
    private BehaviorReportEntity entityReport;

    @BeforeEach
    void setUp() {
        reportId = UUID.randomUUID();
        reporterId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        domainReport = BehaviorReport.builder()
                .id(reportId)
                .reporterId(reporterId)
                .description("Offensive content shared in group chat")
                .reportType(ReportType.OFFENSIVE_CONTENT)
                .status(ReportStatus.PENDING)
                .caseNumber("RPT-20260519-0007")
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityReport = BehaviorReportEntity.builder()
                .id(reportId)
                .reporterId(reporterId)
                .description("Offensive content shared in group chat")
                .reportType(ReportType.OFFENSIVE_CONTENT)
                .status(ReportStatus.PENDING)
                .caseNumber("RPT-20260519-0007")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("save persists report and returns mapped domain")
    void save_persistsAndReturnsDomain() {
        when(mapper.toEntity(domainReport)).thenReturn(entityReport);
        when(jpaRepository.save(entityReport)).thenReturn(entityReport);
        when(mapper.toDomain(entityReport)).thenReturn(domainReport);

        BehaviorReport saved = adapter.save(domainReport);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(reportId);
        assertThat(saved.getCaseNumber()).isEqualTo("RPT-20260519-0007");
        verify(jpaRepository).save(entityReport);
    }

    @Test
    @DisplayName("findById returns Optional with domain report when found")
    void findById_found_returnsOptional() {
        when(jpaRepository.findById(reportId)).thenReturn(Optional.of(entityReport));
        when(mapper.toDomain(entityReport)).thenReturn(domainReport);

        Optional<BehaviorReport> result = adapter.findById(reportId);

        assertThat(result).isPresent();
        assertThat(result.get().getReporterId()).isEqualTo(reporterId);
    }

    @Test
    @DisplayName("findById returns empty Optional when not found")
    void findById_notFound_returnsEmpty() {
        UUID unknownId = UUID.randomUUID();
        when(jpaRepository.findById(unknownId)).thenReturn(Optional.empty());

        Optional<BehaviorReport> result = adapter.findById(unknownId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByReporterId returns all reports for the given reporter")
    void findByReporterId_returnsMappedList() {
        when(jpaRepository.findByReporterId(reporterId)).thenReturn(List.of(entityReport));
        when(mapper.toDomain(entityReport)).thenReturn(domainReport);

        List<BehaviorReport> result = adapter.findByReporterId(reporterId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReporterId()).isEqualTo(reporterId);
        verify(jpaRepository).findByReporterId(reporterId);
    }

    @Test
    @DisplayName("findByReporterId returns empty list when reporter has no reports")
    void findByReporterId_noReports_returnsEmptyList() {
        UUID otherReporter = UUID.randomUUID();
        when(jpaRepository.findByReporterId(otherReporter)).thenReturn(List.of());

        List<BehaviorReport> result = adapter.findByReporterId(otherReporter);

        assertThat(result).isEmpty();
    }
}
