package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.AppointmentMailtoResponse;
import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.application.dto.WellnessResourceRequest;
import edu.eci.patricia.application.dto.WellnessResourceResponse;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import edu.eci.patricia.domain.ports.in.ManageWellnessResourceUseCase;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller exposing wellness resource endpoints (RF23).
 * Supports listing, retrieving, CRUD, and appointment mailto generation for EMOTIONAL_SUPPORT resources.
 */
@RestController
@RequestMapping("/api/v1/wellness/resources")
@RequiredArgsConstructor
@Tag(name = "Wellness Resources", description = "Endpoints for browsing and managing campus wellness resources (RF23)")
@SecurityRequirement(name = "bearerAuth")
public class WellnessResourceController {

    private final GetWellnessResourcesUseCase getResourcesUseCase;
    private final ManageWellnessResourceUseCase manageResourceUseCase;
    private final GetRecommendationsUseCase recommendationsUseCase;

    /**
     * Lists all wellness resources, with an optional category filter (RF23 HU-23-01 / HU-23-02).
     *
     * @param category       optional category filter (EMOTIONAL_SUPPORT, SPORTS, CULTURE, HEALTH, RECOMMENDATIONS, ALL)
     * @param authentication the Spring Security authentication (for recommendations)
     * @return list of wellness resource responses
     */
    @GetMapping
    @Operation(summary = "List all wellness resources",
               description = "Returns all active wellness resources. Optionally filter by category. " +
                             "The RECOMMENDATIONS category returns personalized resources based on the student's survey.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid JWT")
    })
    public ResponseEntity<List<WellnessResourceResponse>> getAllResources(
            @Parameter(description = "Optional category filter", example = "EMOTIONAL_SUPPORT")
            @RequestParam(required = false) WellnessCategory category,
            Authentication authentication) {

        // Handle RECOMMENDATIONS filter (PTR23.2)
        if (category == WellnessCategory.RECOMMENDATIONS) {
            String studentId = (String) authentication.getPrincipal();
            List<WellnessResourceResponse> recommendations = recommendationsUseCase.getRecommendationsForStudent(studentId)
                    .stream()
                    .map(this::mapRecommendationToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(recommendations);
        }

        // Handle ALL or null filter (RN-23.3)
        WellnessCategory filter = (category == WellnessCategory.ALL) ? null : category;

        List<WellnessResourceResponse> resources = getResourcesUseCase.getAllResources(filter)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    private WellnessResourceResponse mapRecommendationToResponse(RecommendationResponse rec) {
        return WellnessResourceResponse.builder()
                .id(UUID.fromString(rec.getId()))
                .name(rec.getName())
                .description(rec.getDescription())
                .category(rec.getCategory())
                .location(rec.getLocation())
                .contactInfo(rec.getContactInfo())
                .schedule(rec.getSchedule())
                .recommendationReason(rec.getRecommendationReason())
                .available(true) // Recommendations only include active resources
                .build();
    }

    /**
     * Retrieves a single wellness resource by its UUID.
     *
     * @param id the UUID of the resource
     * @return the wellness resource details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a wellness resource by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource found"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<WellnessResourceResponse> getResourceById(
            @Parameter(description = "UUID of the wellness resource") @PathVariable UUID id) {

        return getResourcesUseCase.getResourceById(id)
                .map(r -> ResponseEntity.ok(toResponse(r)))
                .orElseThrow(() -> new ResourceNotFoundException("Wellness resource not found with id: " + id));
    }

    /**
     * Generates a pre-built mailto link for requesting a psychological appointment (RF23 HU-23-03).
     * Only available for resources with category EMOTIONAL_SUPPORT. Returns HTTP 400 otherwise.
     *
     * @param id             the UUID of the EMOTIONAL_SUPPORT wellness resource
     * @param authentication the Spring Security authentication (student ID extracted from JWT)
     * @return the mailto components ready for the client to open in an email app
     */
    @GetMapping("/{id}/cita-mailto")
    @Operation(summary = "Generate appointment mailto for a EMOTIONAL_SUPPORT resource",
               description = "Returns pre-built email components (to, subject, body) for requesting a " +
                             "psychological appointment. Only available for EMOTIONAL_SUPPORT resources. " +
                             "The system does NOT send the email — the student does from their email app.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mailto components generated"),
        @ApiResponse(responseCode = "400", description = "Resource is not EMOTIONAL_SUPPORT category"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AppointmentMailtoResponse> getAppointmentMailto(
            @Parameter(description = "UUID of the EMOTIONAL_SUPPORT wellness resource") @PathVariable UUID id,
            Authentication authentication) {

        String studentId = (String) authentication.getPrincipal();
        AppointmentMailtoResponse response = getResourcesUseCase.generateAppointmentMailto(id, studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new wellness resource.
     *
     * @param request the resource creation payload
     * @return the created resource with HTTP 201
     */
    @PostMapping
    @Operation(summary = "Create a wellness resource", description = "Creates a new campus wellness resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<WellnessResourceResponse> createResource(@Valid @RequestBody WellnessResourceRequest request) {
        WellnessResource domain = toDomain(request);
        WellnessResource created = manageResourceUseCase.createResource(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    /**
     * Updates an existing wellness resource.
     *
     * @param id      the UUID of the resource to update
     * @param request the update payload
     * @return the updated resource
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a wellness resource")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<WellnessResourceResponse> updateResource(
            @Parameter(description = "UUID of the resource to update") @PathVariable UUID id,
            @Valid @RequestBody WellnessResourceRequest request) {

        WellnessResource domain = toDomain(request);
        WellnessResource updated = manageResourceUseCase.updateResource(id, domain);
        return ResponseEntity.ok(toResponse(updated));
    }

    /**
     * Deletes a wellness resource by ID.
     *
     * @param id the UUID of the resource to delete
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a wellness resource")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resource deleted"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "UUID of the resource to delete") @PathVariable UUID id) {

        manageResourceUseCase.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    private WellnessResource toDomain(WellnessResourceRequest request) {
        return WellnessResource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .contactInfo(request.getContactInfo())
                .schedule(request.getSchedule())
                .available(request.isAvailable())
                .appointmentEmail(request.getAppointmentEmail())
                .psychologistName(request.getPsychologistName())
                .build();
    }

    private WellnessResourceResponse toResponse(WellnessResource resource) {
        return WellnessResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .category(resource.getCategory())
                .location(resource.getLocation())
                .contactInfo(resource.getContactInfo())
                .schedule(resource.getSchedule())
                .available(resource.isAvailable())
                .appointmentEmail(resource.getAppointmentEmail())
                .psychologistName(resource.getPsychologistName())
                .build();
    }
}
