package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.model.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity mapping to the {@code behavior_reports} table.
 * Records an inappropriate behavior report submitted by a student.
 */
@Entity
@Table(name = "behavior_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorReportEntity {

    /** Primary key generated as a UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Identifier of the student who submitted the report. */
    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    /** Detailed description of the behavior. */
    @Column(nullable = false, length = 2000)
    private String description;

    /** Location where the behavior occurred. */
    private String location;

    /** Classification of the type of behavior. */
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    /** Current processing status of the report. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    /** Timestamp automatically set when the record is inserted. */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp automatically updated on every modification. */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
