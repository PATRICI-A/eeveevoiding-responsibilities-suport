package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.BehaviorReportRequest;
import edu.eci.patricia.application.dto.BehaviorReportResponse;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.exception.WellnessException;
import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.ports.in.SubmitBehaviorReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for inappropriate behavior report operations.
 * Students can submit reports and view their own submissions.
 */
@RestController
@RequestMapping("/api/v1/wellness/reports")
@RequiredArgsConstructor
@Tag(name = "Behavior Reports", description = "Endpoints for submitting and tracking inappropriate behavior reports")
@SecurityRequirement(name = "bearerAuth")
public class BehaviorReportController {

    private final SubmitBehaviorReportUseCase submitBehaviorReportUseCase;

    /**
     * Submits a new inappropriate behavior report for the authenticated student.
     *
     * @param request        the report payload
     * @param authentication the Spring Security authentication holding the student's userId
     * @return the created report with HTTP 201
     */
    @PostMapping
    @Operation(summary = "Submit a behavior report",
               description = "Creates a new report of inappropriate behavior. The reporter ID is taken from the JWT subject.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Report submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BehaviorReportResponse> submitReport(
            @Valid @RequestBody BehaviorReportRequest request,
            Authentication authentication) {

        UUID reporterId = UUID.fromString((String) authentication.getPrincipal());

        BehaviorReport report = BehaviorReport.builder()
                .reporterId(reporterId)
                .description(request.getDescription())
                .location(request.getLocation())
                .reportType(request.getReportType())
                .build();

        BehaviorReport created = submitBehaviorReportUseCase.submitReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    /**
     * Retrieves a behavior report by its UUID. Only the original reporter can view their own report.
     *
     * @param id             the UUID of the report
     * @param authentication the Spring Security authentication
     * @return the report details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a behavior report by ID",
               description = "Returns the report details. Access is restricted to the student who submitted the report.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Report found"),
        @ApiResponse(responseCode = "403", description = "Forbidden — student is not the reporter"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BehaviorReportResponse> getReportById(
            @Parameter(description = "UUID of the report to retrieve") @PathVariable UUID id,
            Authentication authentication) {

        UUID requesterId = UUID.fromString((String) authentication.getPrincipal());

        BehaviorReport report = submitBehaviorReportUseCase.getReportById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Behavior report not found with id: " + id));

        if (!report.getReporterId().equals(requesterId)) {
            throw new WellnessException("Access denied: you are not the reporter of this report");
        }

        return ResponseEntity.ok(toResponse(report));
    }

    /**
     * Returns all behavior reports submitted by the authenticated student.
     *
     * @param authentication the Spring Security authentication
     * @return list of the current student's behavior reports
     */
    @GetMapping("/my-reports")
    @Operation(summary = "List my behavior reports",
               description = "Returns all behavior reports submitted by the currently authenticated student.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BehaviorReportResponse>> getMyReports(Authentication authentication) {
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
                .reporterId(report.getReporterId())
                .description(report.getDescription())
                .location(report.getLocation())
                .reportType(report.getReportType())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
