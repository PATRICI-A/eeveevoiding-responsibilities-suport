package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.request.CreateWellnessResourceRequest;
import edu.eci.patricia.application.dto.request.UpdateWellnessResourceRequest;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.application.mapper.WellnessResourceMapper;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.CreateWellnessResourcePort;
import edu.eci.patricia.domain.ports.in.DeactivateWellnessResourcePort;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcePort;
import edu.eci.patricia.domain.ports.in.UpdateWellnessResourcePort;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bienestar/recursos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Wellness Resources", description = "Endpoints for managing wellness resources")
public class WellnessResourceController {

    private final CreateWellnessResourcePort createWellnessResourcePort;
    private final GetWellnessResourcePort getWellnessResourcePort;
    private final UpdateWellnessResourcePort updateWellnessResourcePort;
    private final DeactivateWellnessResourcePort deactivateWellnessResourcePort;
    private final WellnessResourceMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WellnessResourceResponse> create(
            @RequestBody CreateWellnessResourceRequest request) {
        WellnessResource created = createWellnessResourcePort.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<WellnessResourceResponse>> getAll(
            @RequestParam(required = false) WellnessCategory category) {
        List<WellnessResourceResponse> resources = getWellnessResourcePort.getAll(category)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WellnessResourceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getWellnessResourcePort.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WellnessResourceResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateWellnessResourceRequest request) {
        WellnessResource updated = updateWellnessResourcePort.update(id, mapper.toDomain(id, request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        deactivateWellnessResourcePort.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}