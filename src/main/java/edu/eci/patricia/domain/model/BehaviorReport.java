package edu.eci.patricia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a student-submitted report of inappropriate behavior on campus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorReport {

    /** Unique identifier of this report. */
    private UUID id;

    /** Identifier of the student who submitted the report. */
    private UUID reporterId;

    /** Detailed description of the reported behavior. */
    private String description;

    /** Location where the behavior occurred. */
    private String location;

    /** Classification of the type of behavior reported. */
    private ReportType reportType;

    /** Current processing status of the report. */
    private ReportStatus status;

    /** Timestamp when the report was created. */
    private LocalDateTime createdAt;

    /** Timestamp when the report was last updated. */
    private LocalDateTime updatedAt;
}
