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

    @GetMapping
    @Operation(
            operationId = "getAllWellnessResources",
            summary = "List all wellness resources",
            description = """
                    Returns all active wellness resources available on campus. Supports optional \
                    filtering by category. The RECOMMENDATIONS category returns personalised resources \
                    based on the student's most recent wellness survey (PTR23.2).

                    **Category behaviour:**
                    - `ALL` or `null` — Returns all resources across all categories
                    - `RECOMMENDATIONS` — Returns personalised recommendations based on survey responses (PTR23.2)
                    - `EMOTIONAL_SUPPORT` — Returns only psychological/emotional support resources
                    - `SPORTS` — Returns only sports and physical activity resources
                    - `CULTURE` — Returns only cultural and recreational resources
                    - `HEALTH` — Returns only physical health resources

                    **Fallback:** If a student requests RECOMMENDATIONS but has never completed the survey, \
                    returns all active resources (RN-23.2.2).

                    **Access:** Requires valid JWT Bearer token."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Resources retrieved successfully. Returns an array of wellness resources \
                            matching the requested category. Returns an empty array if no resources \
                            match the filter criteria."""
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid category parameter provided. Valid values: `EMOTIONAL_SUPPORT`, \
                            `SPORTS`, `CULTURE`, `HEALTH`, `RECOMMENDATIONS`, `ALL`."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            No valid JWT Bearer token was provided or the token has expired. \
                            Re-authenticate and retry with a fresh token."""
            )
    })
    public ResponseEntity<List<WellnessResourceResponse>> getAllResources(
            @Parameter(
                    description = """
                            Optional category filter. Valid values:
                            - `EMOTIONAL_SUPPORT` — Psychological and emotional support services
                            - `SPORTS` — Physical activities and sports facilities
                            - `CULTURE` — Cultural events and recreational activities
                            - `HEALTH` — Medical and physical health services
                            - `RECOMMENDATIONS` — Personalised recommendations (PTR23.2)
                            - `ALL` — All categories (default behaviour when omitted)""",
                    example = "EMOTIONAL_SUPPORT",
                    schema = @Schema(implementation = WellnessCategory.class)
            )
            @RequestParam(required = false) WellnessCategory category,
            @Parameter(hidden = true) Authentication authentication) {

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

    @GetMapping("/{id}")
    @Operation(
            operationId = "getWellnessResourceById",
            summary = "Get a wellness resource by ID",
            description = """
                    Retrieves detailed information about a specific wellness resource using its UUID. \
                    Returns all fields including location, contact information, schedule, and availability status.

                    **Use case:** Called when a user clicks on a resource card to view full details, \
                    including appointment email address for EMOTIONAL_SUPPORT resources.

                    **Access:** Requires valid JWT Bearer token."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource found and returned successfully.",
                    content = @Content(schema = @Schema(implementation = WellnessResourceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No wellness resource exists with the provided UUID."
            )
    })
    public ResponseEntity<WellnessResourceResponse> getResourceById(
            @Parameter(
                    description = "UUID of the wellness resource to retrieve",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {

        return getResourcesUseCase.getResourceById(id)
                .map(r -> ResponseEntity.ok(toResponse(r)))
                .orElseThrow(() -> new ResourceNotFoundException("Wellness resource not found with id: " + id));
    }

    @GetMapping("/{id}/cita-mailto")
    @Operation(
            operationId = "generateAppointmentMailto",
            summary = "Generate appointment mailto for an EMOTIONAL_SUPPORT resource",
            description = """
                    Generates a pre-configured `mailto:` link for requesting a psychological appointment (RF23 HU-23-03).
                    This endpoint is only available for resources with category `EMOTIONAL_SUPPORT`.

                    **What it returns:** Email components (recipient, subject, body) that the client can use to \
                    open the user's default email application with the appointment request pre-filled.

                    **What it does NOT do:** The system does NOT send the email automatically. The student must \
                    click the link and send it from their own email client.

                    **Use case:** Called when a student clicks "Request Appointment" on an EMOTIONAL_SUPPORT \
                    resource card. The client uses the returned data to construct a mailto: link.

                    **Access:** Requires valid JWT Bearer token. The student ID is automatically included in \
                    the email body for context."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Mailto components generated successfully. Returns recipient email, subject line, \
                            and pre-filled body text ready for client-side mailto link construction.""",
                    content = @Content(schema = @Schema(implementation = AppointmentMailtoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            The requested resource is not of type EMOTIONAL_SUPPORT. Appointment mailto generation \
                            is only available for psychological support resources."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No wellness resource exists with the provided UUID."
            )
    })
    public ResponseEntity<AppointmentMailtoResponse> getAppointmentMailto(
            @Parameter(
                    description = "UUID of the EMOTIONAL_SUPPORT wellness resource",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(hidden = true) Authentication authentication) {

        String studentId = (String) authentication.getPrincipal();
        AppointmentMailtoResponse response = getResourcesUseCase.generateAppointmentMailto(id, studentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            operationId = "createWellnessResource",
            summary = "Create a wellness resource",
            description = """
                    Creates a new wellness resource in the catalogue. This endpoint is typically used by \
                    administrators to add new support services, activities, or facilities to the platform.

                    **Required fields:** name, description, category, location, contactInfo
                    
                    **Optional fields:** schedule, available (defaults to true), appointmentEmail, psychologistName
                    
                    **Validation:** Name must be unique within the system. Category must be a valid WellnessCategory value.
                    
                    **Access:** Requires ADMINISTRADOR role (implicitly enforced by security configuration)."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Resource created successfully. Returns the created resource with assigned UUID.",
                    content = @Content(schema = @Schema(implementation = WellnessResourceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Validation error. Common causes: missing required fields, duplicate resource name, \
                            invalid category value, or malformed contact information."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Creating resources requires ADMINISTRADOR role."
            )
    })
    public ResponseEntity<WellnessResourceResponse> createResource(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Wellness resource creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = WellnessResourceRequest.class))
            )
            @Valid @RequestBody WellnessResourceRequest request) {
        WellnessResource domain = toDomain(request);
        WellnessResource created = manageResourceUseCase.createResource(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(
            operationId = "updateWellnessResource",
            summary = "Update a wellness resource",
            description = """
                    Updates an existing wellness resource. Partial updates are supported — only provided fields \
                    are updated; omitted fields retain their existing values.

                    **Use case:** Called by administrators to modify resource details such as schedule, \
                    contact information, or availability status.

                    **Validation:** If name is provided, it must be unique (cannot conflict with another resource's name).

                    **Access:** Requires ADMINISTRADOR role."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource updated successfully. Returns the updated resource.",
                    content = @Content(schema = @Schema(implementation = WellnessResourceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error. Common causes: duplicate resource name or invalid category value."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Updating resources requires ADMINISTRADOR role."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No wellness resource exists with the provided UUID."
            )
    })
    public ResponseEntity<WellnessResourceResponse> updateResource(
            @Parameter(
                    description = "UUID of the resource to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Wellness resource update payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = WellnessResourceRequest.class))
            )
            @Valid @RequestBody WellnessResourceRequest request) {

        WellnessResource domain = toDomain(request);
        WellnessResource updated = manageResourceUseCase.updateResource(id, domain);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            operationId = "deleteWellnessResource",
            summary = "Delete a wellness resource",
            description = """
                    Permanently removes a wellness resource from the catalogue. Deletion is physical (hard delete).

                    **Use case:** Called by administrators to remove outdated or deactivated resources.

                    **Note:** Deletion is irreversible. Consider setting `available = false` instead of deletion \
                    for temporary deactivation.

                    **Access:** Requires ADMINISTRADOR role."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Resource deleted successfully. No response body returned."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid JWT Bearer token was provided or the token has expired."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Deleting resources requires ADMINISTRADOR role."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No wellness resource exists with the provided UUID."
            )
    })
    public ResponseEntity<Void> deleteResource(
            @Parameter(
                    description = "UUID of the resource to delete",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {

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
}