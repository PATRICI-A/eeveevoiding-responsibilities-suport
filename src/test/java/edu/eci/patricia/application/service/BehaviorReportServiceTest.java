package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.model.ReportType;
import edu.eci.patricia.domain.ports.out.BehaviorReportRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BehaviorReportService}.
 */
@ExtendWith(MockitoExtension.class)
class BehaviorReportServiceTest {

    @Mock
    private BehaviorReportRepositoryPort reportRepositoryPort;

    @InjectMocks
    private BehaviorReportService service;

    private UUID reporterId;
    private UUID reportId;
    private BehaviorReport sampleReport;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();
        reportId = UUID.randomUUID();

        sampleReport = BehaviorReport.builder()
                .id(reportId)
                .reporterId(reporterId)
                .description("Inappropriate comments were made during the class")
                .location("Room 301, Block C")
                .reportType(ReportType.HARASSMENT)
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("submitReport should set status to PENDING and persist the report")
    void submitReport_validReport_setsPendingStatusAndSaves() {
        BehaviorReport input = BehaviorReport.builder()
                .reporterId(reporterId)
                .description("Bullying observed in hallway")
                .location("Hallway 2nd floor")
                .reportType(ReportType.BULLYING)
                .build();

        when(reportRepositoryPort.save(any(BehaviorReport.class))).thenAnswer(inv -> {
            BehaviorReport r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        BehaviorReport result = service.submitReport(input);

        assertThat(result.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(reportRepositoryPort).save(input);
    }

    @Test
    @DisplayName("getReportById should return the report when it exists")
    void getReportById_existingId_returnsReport() {
        when(reportRepositoryPort.findById(reportId)).thenReturn(Optional.of(sampleReport));

        Optional<BehaviorReport> result = service.getReportById(reportId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(reportId);
        assertThat(result.get().getReportType()).isEqualTo(ReportType.HARASSMENT);
    }

    @Test
    @DisplayName("getReportById should return empty Optional when report does not exist")
    void getReportById_nonExistentId_returnsEmpty() {
        UUID nonExistentId = UUID.randomUUID();
        when(reportRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<BehaviorReport> result = service.getReportById(nonExistentId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getReportsByReporter should return all reports for the given reporter")
    void getReportsByReporter_validReporterId_returnsReports() {
        when(reportRepositoryPort.findByReporterId(reporterId)).thenReturn(List.of(sampleReport));

        List<BehaviorReport> results = service.getReportsByReporter(reporterId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getReporterId()).isEqualTo(reporterId);
    }

    @Test
    @DisplayName("getReportsByReporter should return empty list when reporter has no reports")
    void getReportsByReporter_noReports_returnsEmptyList() {
        UUID anotherReporterId = UUID.randomUUID();
        when(reportRepositoryPort.findByReporterId(anotherReporterId)).thenReturn(List.of());

        List<BehaviorReport> results = service.getReportsByReporter(anotherReporterId);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("submitReport should set both createdAt and updatedAt timestamps")
    void submitReport_setsTimestamps() {
        BehaviorReport input = BehaviorReport.builder()
                .reporterId(reporterId)
                .description("Discrimination in cafeteria")
                .reportType(ReportType.DISCRIMINATION)
                .build();

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        when(reportRepositoryPort.save(any(BehaviorReport.class))).thenAnswer(inv -> inv.getArgument(0));

        BehaviorReport result = service.submitReport(input);

        assertThat(result.getCreatedAt()).isAfterOrEqualTo(before);
        assertThat(result.getUpdatedAt()).isAfterOrEqualTo(before);
    }
}
