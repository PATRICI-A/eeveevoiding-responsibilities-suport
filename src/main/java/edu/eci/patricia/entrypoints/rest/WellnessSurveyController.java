package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.application.dto.RecommendedCategoryResponse;
import edu.eci.patricia.application.dto.SurveyAnswerRequest;
import edu.eci.patricia.application.dto.SurveyResultResponse;
import edu.eci.patricia.application.dto.SurveySubmissionRequest;
import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.SubmitSurveyUseCase;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for wellness survey operations (PTR23.1 / PTR23.2).
 */
@RestController
@RequestMapping("/api/v1/bienestar/encuesta")
@RequiredArgsConstructor
@Tag(name = "Wellness Survey", description = "Encuesta de bienestar estudiantil (PTR23.1) y recomendaciones (PTR23.2)")
@SecurityRequirement(name = "bearerAuth")
public class WellnessSurveyController {

    /** All mandatory question IDs (P01–P10). */
    private static final Set<String> MANDATORY_QUESTIONS = Set.of(
            "P01", "P02", "P03", "P04", "P05",
            "P06", "P07", "P08", "P09", "P10");

    private final SubmitSurveyUseCase submitSurveyUseCase;
    private final GetRecommendationsUseCase recommendationsUseCase;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/bienestar/encuesta  — PTR23.1
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(
            operationId = "submitWellnessSurvey",
            summary = "Enviar encuesta de bienestar",
            description = """
                    Recibe las 10 respuestas obligatorias del cuestionario de bienestar estudiantil (P01–P10), \
                    las persiste en el sistema, y retorna las categorías de recursos recomendadas según las \
                    respuestas proporcionadas (PTR23.1 / PTR23.2).

                    **Reglas de negocio:**
                    - Las 10 preguntas (P01 a P10) son obligatorias (RN-23.1.1)
                    - Si falta alguna pregunta, la solicitud es rechazada con HTTP 400
                    - Cada estudiante puede tener múltiples respuestas; la más reciente se usa para recomendaciones
                    - Las respuestas se almacenan de forma anónima para análisis institucional

                    **Procesamiento posterior:**
                    - Las respuestas se analizan mediante un motor de reglas
                    - Se determinan las categorías de recursos más adecuadas para el estudiante
                    - Las recomendaciones se devuelven inmediatamente y también se guardan

                    **Access:** Requiere token JWT válido. El ID del estudiante se extrae del token."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = """
                            Encuesta registrada exitosamente. Retorna el ID de la respuesta guardada \
                            y las categorías recomendadas basadas en las respuestas.""",
                    content = @Content(schema = @Schema(implementation = SurveyResultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Una o más preguntas obligatorias no fueron respondidas. Todas las preguntas \
                            P01 a P10 son requeridas (RN-23.1.1)."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT ausente, inválido o expirado. Re-autentíquese y reintente."
            )
    })
    public ResponseEntity<SurveyResultResponse> submitSurvey(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Payload de la encuesta. Debe incluir exactamente las 10 preguntas (P01–P10) \
                            con sus respectivas respuestas. Las respuestas deben ser válidas según las \
                            opciones definidas para cada pregunta.""",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SurveySubmissionRequest.class))
            )
            @Valid @RequestBody SurveySubmissionRequest request,
            @Parameter(hidden = true) Authentication authentication) {

        String studentId = (String) authentication.getPrincipal();

        // Validate that all mandatory questions are covered (RN-23.1.1)
        Set<String> submittedIds = request.getSurveyResponses().stream()
                .map(SurveyAnswerRequest::getQuestionId)
                .collect(Collectors.toSet());
        List<String> missingQuestions = MANDATORY_QUESTIONS.stream()
                .filter(q -> !submittedIds.contains(q))
                .sorted()
                .collect(Collectors.toList());
        if (!missingQuestions.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Build answers map
        Map<String, String> answers = request.getSurveyResponses().stream()
                .collect(Collectors.toMap(
                        SurveyAnswerRequest::getQuestionId,
                        SurveyAnswerRequest::getAnswer));

        SurveyResponse domain = SurveyResponse.builder()
                .studentId(studentId)
                .answers(answers)
                .build();

        SurveyResponse saved = submitSurveyUseCase.submitSurvey(domain);

        // Generate recommended categories (PTR23.2)
        List<RecommendedCategoryResponse> recommendedCategories =
                recommendationsUseCase.getRecommendedCategories(answers);

        SurveyResultResponse result = SurveyResultResponse.builder()
                .surveyId(saved.getId())
                .recommendedCategories(recommendedCategories)
                .submittedAt(saved.getSubmittedAt())
                .message("Encuesta registrada. Revisa tus recomendaciones de bienestar.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/bienestar/encuesta/preguntas  — PTR23.1 (question retrieval)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/preguntas")
    @Operation(
            operationId = "getSurveyQuestions",
            summary = "Obtener preguntas de la encuesta",
            description = """
                    Retorna el catálogo completo de las 10 preguntas del cuestionario de bienestar estudiantil, \
                    cada una con sus opciones de respuesta válidas (PTR23.1, paso 2).

                    **Estructura de cada pregunta:**
                    - `questionId`: Identificador único (P01 a P10)
                    - `questionText`: Texto de la pregunta en español
                    - `options`: Lista de opciones de respuesta válidas

                    **Uso:** El cliente debe llamar este endpoint antes de mostrar el formulario de encuesta \
                    para obtener las preguntas y opciones actualizadas.

                    **Access:** Requiere token JWT válido."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Preguntas retornadas exitosamente. Lista de 10 preguntas con sus opciones."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT ausente, inválido o expirado."
            )
    })
    public ResponseEntity<List<SurveyQuestionDto>> getSurveyQuestions() {
        return ResponseEntity.ok(buildQuestions());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/bienestar/encuesta/recomendaciones  — PTR23.2
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/recomendaciones")
    @Operation(
            operationId = "getWellnessRecommendations",
            summary = "Obtener recomendaciones de bienestar",
            description = """
                    Retorna recursos de bienestar personalizados para el estudiante autenticado basados en \
                    su encuesta más reciente (PTR23.2).

                    **Comportamiento:**
                    - Si el estudiante ha completado al menos una encuesta → recursos recomendados según respuestas
                    - Si el estudiante NO ha completado ninguna encuesta → retorna todos los recursos activos \
                      como fallback (RN-23.2.2)

                    **Recomendación:** Cada recurso incluye un campo `recommendationReason` que explica por \
                    qué fue recomendado (basado en las respuestas del estudiante).

                    **Access:** Requiere token JWT válido."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Recomendaciones retornadas exitosamente. Retorna lista de recursos de bienestar \
                            ordenados por relevancia. Si no hay encuesta previa, retorna todos los recursos activos."""
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT ausente, inválido o expirado."
            )
    })
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @Parameter(hidden = true) Authentication authentication) {
        String studentId = (String) authentication.getPrincipal();
        List<RecommendationResponse> recommendations =
                recommendationsUseCase.getRecommendationsForStudent(studentId);
        return ResponseEntity.ok(recommendations);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Survey questions catalogue
    // ─────────────────────────────────────────────────────────────────────────

    private List<SurveyQuestionDto> buildQuestions() {
        return List.of(
                q("P01", "¿Cómo describirías tu estado de ánimo general esta semana?",
                        List.of("Muy bien", "Bien", "Regular", "Mal", "Muy mal")),
                q("P02", "¿Qué tan estresado/a te has sentido por tus responsabilidades académicas?",
                        List.of("1", "2", "3", "4", "5")),
                q("P03", "¿Con qué frecuencia realizas actividad física durante la semana?",
                        List.of("Todos los días", "3–4 veces", "1–2 veces", "Nunca")),
                q("P04", "¿Cómo calificarías la calidad de tu sueño últimamente?",
                        List.of("Muy buena", "Buena", "Regular", "Mala", "Muy mala")),
                q("P05", "¿Mantienes hábitos alimenticios regulares y saludables?",
                        List.of("Sí, siempre", "La mayoría de las veces", "A veces", "Casi nunca", "No")),
                q("P06", "¿Sientes que tienes apoyo social suficiente (amigos, familia, compañeros)?",
                        List.of("Sí, completamente", "En su mayoría sí", "No mucho", "No, me siento solo/a")),
                q("P07", "¿Participas en actividades culturales, sociales o recreativas del campus?",
                        List.of("Con frecuencia", "A veces", "Rara vez", "Nunca")),
                q("P08", "¿Qué tan satisfecho/a estás con tu rendimiento académico actual?",
                        List.of("1", "2", "3", "4", "5")),
                q("P09", "¿Has experimentado síntomas físicos como fatiga, dolor de cabeza o tensión muscular?",
                        List.of("Nunca", "A veces", "Con frecuencia", "Casi siempre")),
                q("P10", "¿En qué áreas crees que necesitas más apoyo? (puedes elegir varias)",
                        List.of("Actividad física", "Alimentación", "Salud física",
                                "Manejo del estrés", "Apoyo emocional", "Integración social", "Ninguna"))
        );
    }

    private SurveyQuestionDto q(String id, String text, List<String> options) {
        return new SurveyQuestionDto(id, text, options);
    }

    /** Simple DTO for question data. */
    @Schema(
            name = "SurveyQuestion",
            description = "Pregunta del cuestionario de bienestar con sus opciones de respuesta"
    )
    public record SurveyQuestionDto(
            @Schema(description = "Identificador único de la pregunta (P01 a P10)", example = "P01")
            String questionId,
            @Schema(description = "Texto de la pregunta en español", example = "¿Cómo describirías tu estado de ánimo general esta semana?")
            String questionText,
            @Schema(description = "Lista de opciones de respuesta válidas para esta pregunta")
            List<String> options
    ) {}
}