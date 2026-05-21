package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.model.ReportType;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.BehaviorReportEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BehaviorReportMapperTest {

    private BehaviorReportMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BehaviorReportMapper();
    }

    @Test
    @DisplayName("toDomain converts entity to domain model correctly")
    void toDomain_validEntity_returnsDomainModel() {
        UUID id = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        BehaviorReportEntity entity = BehaviorReportEntity.builder()
                .id(id)
                .reporterId(reporterId)
                .description("Harassment incident in classroom")
                .reportType(ReportType.HARASSMENT)
                .referenceId("some-event-id")
                .status(ReportStatus.PENDING)
                .caseNumber("RPT-20260519-0042")
                .createdAt(now)
                .updatedAt(now)
                .build();

        BehaviorReport domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getReporterId()).isEqualTo(reporterId);
        assertThat(domain.getDescription()).isEqualTo("Harassment incident in classroom");
        assertThat(domain.getReportType()).isEqualTo(ReportType.HARASSMENT);
        assertThat(domain.getReferenceId()).isEqualTo("some-event-id");
        assertThat(domain.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(domain.getCaseNumber()).isEqualTo("RPT-20260519-0042");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toDomain returns null when entity is null")
    void toDomain_nullEntity_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toEntity converts domain model to entity correctly")
    void toEntity_validDomain_returnsEntity() {
        UUID id = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        BehaviorReport domain = BehaviorReport.builder()
                .id(id)
                .reporterId(reporterId)
                .description("Inappropriate behavior in the cafeteria")
                .reportType(ReportType.INAPPROPRIATE_BEHAVIOR)
                .status(ReportStatus.UNDER_REVIEW)
                .caseNumber("RPT-20260519-0099")
                .createdAt(now)
                .updatedAt(now)
                .build();

        BehaviorReportEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getReporterId()).isEqualTo(reporterId);
        assertThat(entity.getDescription()).isEqualTo("Inappropriate behavior in the cafeteria");
        assertThat(entity.getReportType()).isEqualTo(ReportType.INAPPROPRIATE_BEHAVIOR);
        assertThat(entity.getStatus()).isEqualTo(ReportStatus.UNDER_REVIEW);
        assertThat(entity.getCaseNumber()).isEqualTo("RPT-20260519-0099");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toEntity returns null when domain is null")
    void toEntity_nullDomain_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Round-trip preserves all report types and statuses")
    void roundTrip_entityToDomainToEntity_preservesTypeAndStatus() {
        for (ReportType type : ReportType.values()) {
            BehaviorReportEntity original = BehaviorReportEntity.builder()
                    .id(UUID.randomUUID())
                    .reporterId(UUID.randomUUID())
                    .description("Test description")
                    .reportType(type)
                    .status(ReportStatus.RESOLVED)
                    .caseNumber("RPT-20260519-0001")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            BehaviorReportEntity roundTripped = mapper.toEntity(mapper.toDomain(original));

            assertThat(roundTripped.getReportType()).isEqualTo(type);
            assertThat(roundTripped.getStatus()).isEqualTo(ReportStatus.RESOLVED);
            assertThat(roundTripped.getCaseNumber()).isEqualTo("RPT-20260519-0001");
        }
    }
}
