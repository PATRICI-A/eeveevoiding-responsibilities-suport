package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing a behavior report returned to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Behavior report details returned by the API")
public class BehaviorReportResponse {

    @Schema(description = "Unique identifier of the report")
    private UUID id;

    @Schema(description = "Identifier of the student who submitted the report")
    private UUID reporterId;

    @Schema(description = "Description of the inappropriate behavior")
    private String description;

    @Schema(description = "Location where the behavior occurred")
    private String location;

    @Schema(description = "Type of behavior reported", example = "HARASSMENT")
    private ReportType reportType;

    @Schema(description = "Current processing status of the report", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "Timestamp when the report was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the report was last updated")
    private LocalDateTime updatedAt;
}
