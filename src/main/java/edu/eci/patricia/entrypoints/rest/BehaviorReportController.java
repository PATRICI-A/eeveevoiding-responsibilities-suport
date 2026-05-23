package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.BehaviorReportRequest;
import edu.eci.patricia.application.dto.BehaviorReportResponse;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.exception.WellnessException;
import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.ports.in.SubmitBehaviorReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for inappropriate behavior report operations (RF24).
 * Students can submit anonymous reports and view their own submissions.
 * Reporter identity is never exposed to the reported party.
 */
@RestController
@RequestMapping("/api/v1/wellness/reports")
@RequiredArgsConstructor
@Tag(name = "Behavior Reports", description = "Endpoints for submitting and tracking inappropriate behavior reports (RF24)")
@SecurityRequirement(name = "bearerAuth")
public class BehaviorReportController {

    private final SubmitBehaviorReportUseCase submitBehaviorReportUseCase;

    @PostMapping
    @Operation(
            operationId = "submitBehaviorReport",
            summary = "Submit a behavior report",
            description = """
                    Creates a new report of inappropriate behavior (RF24). Reports are submitted anonymously \
                    from the perspective of the reporter — the reporter's identity is stored for tracking \
                    but is never exposed to the reported party.

                    **Response:** Upon successful submission, the system returns:
                    - A unique case number in format `RPT-YYYYMMDD-XXXX` (e.g., RPT-20250615-0001)
                    - The report ID (UUID) for future reference
                    - Current status (initially `PENDING`)

                    **Use case:** Called when a student encounters inappropriate content or behavior \
                    within the platform (messages, posts, or user interactions).

                    **Privacy:** The reporter's identity is encrypted in storage. Support staff can see \
                    the reporter ID for follow-up purposes, but the reported user never sees who reported them.

                    **Access:** Requires valid JWT Bearer token. The reporter ID is extracted from the token."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = """
                            Report submitted successfully. Returns the created report with a unique case number. \
                            The case number should be shown to the user for reference.""",
                    content = @Content(schema = @Schema(implementation = BehaviorReportResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Validation error. Common causes: missing required fields (reportType, description), \
                            invalid reportType value, or description exceeding maximum length."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            )
    })
    public ResponseEntity<BehaviorReportResponse> submitReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Payload containing report details. Required fields: `reportType` and `description`. \
                            Optional fields: `referenceId` (e.g., message ID or post ID) to link the report \
                            to specific content.""",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BehaviorReportRequest.class))
            )
            @Valid @RequestBody BehaviorReportRequest request,
            @Parameter(hidden = true) Authentication authentication) {

        UUID reporterId = UUID.fromString((String) authentication.getPrincipal());

        BehaviorReport report = BehaviorReport.builder()
                .reporterId(reporterId)
                .reportType(request.getReportType())
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .build();

        BehaviorReport created = submitBehaviorReportUseCase.submitReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(
            operationId = "getBehaviorReportById",
            summary = "Get a behavior report by ID",
            description = """
                    Retrieves detailed information about a specific behavior report using its UUID.

                    **Access control:** Only the student who submitted the report can view its details. \
                    A student cannot view reports submitted by other users (HTTP 403).

                    **Use case:** Called when a student wants to check the status of a previously \
                    submitted report or view the assigned case number.

                    **Access:** Requires valid JWT Bearer token."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Report found and returned successfully.",
                    content = @Content(schema = @Schema(implementation = BehaviorReportResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. The authenticated user is not the reporter of this report."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No behavior report exists with the provided UUID."
            )
    })
    public ResponseEntity<BehaviorReportResponse> getReportById(
            @Parameter(
                    description = "UUID of the report to retrieve",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(hidden = true) Authentication authentication) {

        UUID requesterId = UUID.fromString((String) authentication.getPrincipal());

        BehaviorReport report = submitBehaviorReportUseCase.getReportById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Behavior report not found with id: " + id));

        if (!report.getReporterId().equals(requesterId)) {
            throw new WellnessException("Access denied: you are not the reporter of this report");
        }

        return ResponseEntity.ok(toResponse(report));
    }

    @GetMapping("/my-reports")
    @Operation(
            operationId = "getMyBehaviorReports",
            summary = "List my behavior reports",
            description = """
                    Returns all behavior reports submitted by the currently authenticated student. \
                    Use this endpoint to populate a history of past reports on the student's profile.

                    **Pagination:** Results are returned as a list (default sorting by creation date descending). \
                    If pagination is needed in the future, consider adding `page` and `size` parameters.

                    **Empty result:** Returns an empty list `[]` if the student has never submitted any reports — \
                    never HTTP 404.

                    **Access:** Requires valid JWT Bearer token."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Reports retrieved successfully. Returns an array of behavior reports submitted \
                            by the authenticated student. Returns an empty array if no reports exist."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            )
    })
    public ResponseEntity<List<BehaviorReportResponse>> getMyReports(
            @Parameter(hidden = true) Authentication authentication) {
        UUID reporterId = UUID.fromString((String) authentication.getPrincipal());

        List<BehaviorReportResponse> reports = submitBehaviorReportUseCase.getReportsByReporter(reporterId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    // -------------------------------------------------------------------------
    // Mapping helper
    // -------------------------------------------------------------------------

    private BehaviorReportResponse toResponse(BehaviorReport report) {
        return BehaviorReportResponse.builder()
                .id(report.getId())
                .caseNumber(report.getCaseNumber())
                .message(report.getCaseNumber() != null
                        ? "Tu reporte ha sido recibido. Número de caso: " + report.getCaseNumber()
                        : null)
                .reporterId(report.getReporterId())
                .reportType(report.getReportType())
                .description(report.getDescription())
                .referenceId(report.getReferenceId())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}