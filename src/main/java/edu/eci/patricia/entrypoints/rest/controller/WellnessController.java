package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.response.AppointmentMailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.application.mapper.WellnessMapper;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.GetAppointmentMailtoUseCase;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Bienestar y Soporte",
        description = "RF23 — Directorio de recursos de apoyo estudiantil e integración de cita psicológica")
@RestController
@RequestMapping("/api/v1/bienestar")
@RequiredArgsConstructor
public class WellnessController {

    private final GetWellnessResourcesUseCase getWellnessResourcesUseCase;
    private final GetAppointmentMailtoUseCase getAppointmentMailtoUseCase;
    private final WellnessMapper mapper;

    @Operation(
            summary = "Listar recursos de bienestar",
            description = "Retorna todos los recursos institucionales activos. " +
                    "Si se indica `categoryFilter`, filtra por categoría. RF23 — RN-23.2, RN-23.3.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de recursos (puede ser vacía)"),
            @ApiResponse(responseCode = "400", description = "Categoría inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido")
    })
    @GetMapping("/recursos")
    public ResponseEntity<List<WellnessResourceResponse>> getResources(
            Authentication authentication,

            @Parameter(description = "Filtrar por categoría: MENTAL_HEALTH | SPORTS | CULTURE | ACADEMIC_SUPPORT")
            @RequestParam(required = false) WellnessCategory categoryFilter) {

        String userId = (String) authentication.getPrincipal();

        List<WellnessResourceResponse> response = getWellnessResourcesUseCase
                .execute(categoryFilter)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Generar enlace de cita psicológica",
            description = "RF23 — RN-23.4 / RN-23.5.\n\n" +
                    "Genera un enlace `mailto` pre-armado con el nombre del estudiante " +
                    "extraído directamente del JWT.\n\n" +
                    "**Solo disponible para recursos de categoría `MENTAL_HEALTH`.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enlace mailto generado correctamente"),
            @ApiResponse(responseCode = "400", description = "El recurso no es de categoría MENTAL_HEALTH"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @GetMapping("/recursos/{id}/cita-mailto")
    public ResponseEntity<AppointmentMailtoResponse> getAppointmentMailto(
            Authentication authentication,

            @Parameter(description = "ID del recurso MENTAL_HEALTH", example = "1")
            @PathVariable String id) {

        String studentName = (String) authentication.getDetails();

        return ResponseEntity.ok(
                mapper.toMailtoResponse(
                        getAppointmentMailtoUseCase.execute(id, studentName)));
    }
}