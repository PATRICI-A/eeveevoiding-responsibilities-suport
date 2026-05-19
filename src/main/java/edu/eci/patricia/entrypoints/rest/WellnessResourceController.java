package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.WellnessResourceRequest;
import edu.eci.patricia.application.dto.WellnessResourceResponse;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
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
 * REST controller exposing wellness resource endpoints.
 * Supports listing, retrieving, creating, updating, and deleting campus wellness resources.
 */
@RestController
@RequestMapping("/api/v1/wellness/resources")
@RequiredArgsConstructor
@Tag(name = "Wellness Resources", description = "Endpoints for browsing and managing campus wellness resources")
@SecurityRequirement(name = "bearerAuth")
public class WellnessResourceController {

    private final GetWellnessResourcesUseCase getResourcesUseCase;
    private final ManageWellnessResourceUseCase manageResourceUseCase;

    /**
     * Lists all wellness resources, with an optional category filter.
     *
     * @param category optional category to filter results
     * @return list of wellness resource responses
     */
    @GetMapping
    @Operation(summary = "List all wellness resources",
               description = "Returns all available wellness resources. Optionally filter by category.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid JWT")
    })
    public ResponseEntity<List<WellnessResourceResponse>> getAllResources(
            @Parameter(description = "Optional category filter", example = "MENTAL_HEALTH")
            @RequestParam(required = false) WellnessCategory category) {

        List<WellnessResourceResponse> resources = getResourcesUseCase.getAllResources(category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
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
     * Creates a new wellness resource.
     *
     * @param request the resource creation payload
     * @return the created resource with HTTP 201
     */
    @PostMapping
    @Operation(summary = "Create a wellness resource", description = "Creates a new campus wellness resource (admin only).")
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
                .createdAt(LocalDateTime.now())
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
                .createdAt(resource.getCreatedAt())
                .build();
    }
}
