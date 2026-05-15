package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.request.CreateWellnessResourceRequest;
import edu.eci.patricia.application.dto.request.UpdateWellnessResourceRequest;
import edu.eci.patricia.application.dto.response.MailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.application.mapper.WellnessResourceMapper;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.*;
import edu.eci.patricia.domain.valueobjects.MailtoAppointment;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bienestar/recursos")
@RequiredArgsConstructor
public class WellnessController {

    private final GetWellnessResourcesPort   getWellnessResourcesPort;
    private final CreateWellnessResourcePort createWellnessResourcePort;
    private final UpdateWellnessResourcePort updateWellnessResourcePort;
    private final DeleteWellnessResourcePort deleteWellnessResourcePort;
    private final GenerateMailtoPort         generateMailtoPort;
    private final WellnessResourceMapper     mapper;

    @GetMapping
    public ResponseEntity<List<WellnessResourceResponse>> getAll(
            @RequestParam(required = false) WellnessCategory categoryFilter
    ) {
        List<WellnessResourceResponse> response = getWellnessResourcesPort
                .getAll(Optional.ofNullable(categoryFilter))
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WellnessResourceResponse> getById(@PathVariable String id) {
        WellnessResource resource = getWellnessResourcesPort.getById(ResourceId.of(id));
        return ResponseEntity.ok(mapper.toResponse(resource));
    }

    @PostMapping
    public ResponseEntity<WellnessResourceResponse> create(
            @RequestBody CreateWellnessResourceRequest request
    ) {
        WellnessResource resource = createWellnessResourcePort.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(resource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WellnessResourceResponse> update(
            @PathVariable String id,
            @RequestBody UpdateWellnessResourceRequest request
    ) {
        WellnessResource incoming = WellnessResource.createGeneral(
                request.name(),
                request.description(),
                request.contact(),
                request.schedule(),
                null
        );
        WellnessResource updated = updateWellnessResourcePort.update(ResourceId.of(id), incoming);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteWellnessResourcePort.delete(ResourceId.of(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cita-mailto")
    public ResponseEntity<MailtoResponse> generateMailto(
            @PathVariable String id,
            @RequestHeader("X-Student-Name") String studentName
    ) {
        MailtoAppointment appointment = generateMailtoPort.generate(ResourceId.of(id), studentName);
        return ResponseEntity.ok(mapper.toMailtoResponse(appointment));
    }
}