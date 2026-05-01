package edu.eci.patricia.bienestar.infrastructure.controller;

import edu.eci.patricia.bienestar.application.service.RecursoService;
import edu.eci.patricia.bienestar.domain.model.RecursoBienestar;
import edu.eci.patricia.bienestar.domain.model.TipoRecurso;
import edu.eci.patricia.bienestar.infrastructure.dto.RecursoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bienestar/recursos")
@RequiredArgsConstructor
@Tag(name = "Bienestar y Soporte", description = "Directorio de recursos institucionales de apoyo estudiantil disponibles en la Escuela")
public class RecursoController {

    private final RecursoService recursoService;

    @GetMapping
    @Operation(
        summary = "Consultar recursos de bienestar",
        description = "Como estudiante, quiero ver el listado de recursos institucionales de apoyo disponibles en la Escuela, para acceder a los servicios que necesito según mi situación."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de recursos activos retornado exitosamente",
            content = @Content(schema = @Schema(implementation = RecursoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Categoría inválida", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content)
    })
    public ResponseEntity<List<RecursoResponse>> listarRecursos(
            @Parameter(description = "Filtrar por categoría: SALUD_MENTAL, DEPORTES, CULTURA, APOYO_ACADEMICO")
            @RequestParam(required = false) TipoRecurso categoria) {

        List<RecursoBienestar> recursos = categoria != null
                ? recursoService.obtenerPorCategoria(categoria)
                : recursoService.obtenerTodos();

        List<RecursoResponse> response = recursos.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private RecursoResponse toResponse(RecursoBienestar recurso) {
        RecursoResponse dto = new RecursoResponse();
        dto.setId(recurso.getId());
        dto.setNombre(recurso.getNombre());
        dto.setDescripcion(recurso.getDescripcion());
        dto.setCategoria(recurso.getCategoria());
        dto.setContacto(recurso.getContacto());
        dto.setHorario(recurso.getHorario());
        dto.setUbicacion(recurso.getUbicacion());
        dto.setActivo(recurso.getActivo());
        return dto;
    }
}
