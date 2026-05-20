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
 * Response DTO representing a behavior report returned to the client (RF24).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Behavior report details returned by the API")
public class BehaviorReportResponse {

    @Schema(description = "Unique identifier of the report")
    private UUID id;

    @Schema(description = "Unique case number assigned by the system", example = "RPT-20260519-4823")
    private String caseNumber;

    @Schema(description = "Confirmation message", example = "Tu reporte ha sido recibido. Número de caso: RPT-20260519-4823")
    private String message;

    @Schema(description = "Identifier of the student who submitted the report")
    private UUID reporterId;

    @Schema(description = "Type of behavior reported", example = "HARASSMENT")
    private ReportType reportType;

    @Schema(description = "Description of the inappropriate behavior")
    private String description;

    @Schema(description = "Optional reference ID (event or user) involved")
    private String referenceId;

    @Schema(description = "Current processing status of the report", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "Timestamp when the report was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the report was last updated")
    private LocalDateTime updatedAt;
}
