package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.response.AppointmentMailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.application.mapper.WellnessMapper;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.GetAppointmentMailtoUseCase;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(
        name = "Bienestar y Soporte",
        description = "RF23 — Directorio de recursos de apoyo estudiantil e integración de cita psicológica"
)
@RestController
@RequestMapping("/api/v1/bienestar")
@RequiredArgsConstructor
public class WellnessController {

    private final GetWellnessResourcesUseCase getWellnessResourcesUseCase;
    private final GetAppointmentMailtoUseCase getAppointmentMailtoUseCase;
    private final WellnessMapper mapper;

    @Operation(
            summary = "Listar recursos de bienestar",
            description = """
                    Retorna todos los recursos institucionales activos registrados en la base de datos.
                    
                    Si se indica `categoryFilter`, filtra por categoría. **RF23 — RN-23.2, RN-23.3.**
                    
                    Categorías disponibles:
                    - `MENTAL_HEALTH` — Psicología y salud mental
                    - `SPORTS` — Canchas y deportes
                    - `CULTURE` — Actividades culturales
                    - `ACADEMIC_SUPPORT` — Tutorías y apoyo académico
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de recursos activos",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = WellnessResourceResponse.class)),
                            examples = @ExampleObject(
                                    name = "Respuesta con un recurso MENTAL_HEALTH",
                                    value = """
                                            [
                                              {
                                                "id": "1",
                                                "name": "Servicio de Psicología",
                                                "description": "Apoyo psicológico individual para estudiantes",
                                                "contactPhone": "601-668-3600 ext. 1234",
                                                "contactEmail": "bienestar@escuelaing.edu.co",
                                                "schedule": "Lunes a viernes, 8:00 a.m. – 5:00 p.m.",
                                                "category": "MENTAL_HEALTH",
                                                "appointmentEmail": "psicologia@escuelaing.edu.co",
                                                "psychologistName": "Dra. Andrea Gómez"
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Categoría inválida")
    })
    @GetMapping("/recursos")
    public ResponseEntity<List<WellnessResourceResponse>> getResources(
            @Parameter(description = "Filtrar por categoría. Si se omite, retorna todos los recursos activos.",
                    example = "MENTAL_HEALTH")
            @RequestParam(required = false) WellnessCategory categoryFilter) {

        List<WellnessResourceResponse> response = getWellnessResourcesUseCase
                .execute(categoryFilter)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Generar enlace de cita psicológica (mailto)",
            description = """
                    
                    Genera un enlace `mailto` pre-armado con el nombre del estudiante.
                    
                    **Solo disponible para recursos de categoría `MENTAL_HEALTH`.**
                    Para cualquier otra categoría retorna `400 Bad Request`.
                    
                    > ⚠️ Temporal: el nombre del estudiante se recibe por header `X-User-Name`
                    > mientras se integra el JWT con el Equipo 1.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Enlace mailto generado correctamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AppointmentMailtoResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "resourceId": "1",
                                              "psychologistName": "Dra. Andrea Gómez",
                                              "appointmentEmail": "psicologia@escuelaing.edu.co",
                                              "subject": "Solicitud de cita - Laura González",
                                              "body": "Estimada Dra. Andrea Gómez, mi nombre es Laura González...",
                                              "mailtoLink": "mailto:psicologia@escuelaing.edu.co?subject=Solicitud&body=..."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "El recurso no es de categoría MENTAL_HEALTH"),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @GetMapping("/recursos/{id}/cita-mailto")
    public ResponseEntity<AppointmentMailtoResponse> getAppointmentMailto(
            @Parameter(description = "ID del recurso. Debe ser categoría MENTAL_HEALTH.", example = "1")
            @PathVariable String id,

            @Parameter(description = "Nombre del estudiante (temporal hasta integrar JWT)",
                    example = "Laura González")
            @RequestHeader(value = "X-User-Name", defaultValue = "Estudiante") String studentName) {

        return ResponseEntity.ok(
                mapper.toMailtoResponse(
                        getAppointmentMailtoUseCase.execute(id, studentName)));
    }
}