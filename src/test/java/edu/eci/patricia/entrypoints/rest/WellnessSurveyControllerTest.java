package edu.eci.patricia.entrypoints.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.eci.patricia.application.dto.SurveySubmissionRequest;
import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellbeingLevel;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.SubmitSurveyUseCase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link WellnessSurveyController} with full Spring Security context.
 * Uses real JWT tokens so the JwtAuthFilter can authenticate requests correctly.
 * Service layer is mocked to focus on controller logic.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:surveytestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test-secret-for-ci-only-at-least-32-chars"
})
class WellnessSurveyControllerTest {

    private static final String JWT_SECRET = "test-secret-for-ci-only-at-least-32-chars";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private SubmitSurveyUseCase submitSurveyUseCase;

    @MockitoBean
    private GetRecommendationsUseCase recommendationsUseCase;

    private String bearerToken;
    private UUID userId;
    private SurveySubmissionRequest validRequest;
    private SurveyResponse savedSurvey;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        userId = UUID.randomUUID();
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        bearerToken = "Bearer " + Jwts.builder()
                .subject(userId.toString())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        validRequest = SurveySubmissionRequest.builder()
                .moodScore(4)
                .stressScore(3)
                .sleepScore(4)
                .socialScore(3)
                .academicScore(4)
                .build();

        savedSurvey = SurveyResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .moodScore(4)
                .stressScore(3)
                .sleepScore(4)
                .socialScore(3)
                .academicScore(4)
                .averageScore(3.6)
                .wellbeingLevel(WellbeingLevel.GOOD)
                .submittedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/wellness/survey/submit returns 201 with result")
    void submitSurvey_valid_returns201() throws Exception {
        when(submitSurveyUseCase.submitSurvey(any(SurveyResponse.class))).thenReturn(savedSurvey);
        when(recommendationsUseCase.getRecommendations(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/v1/wellness/survey/submit")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.averageScore").value(3.6))
                .andExpect(jsonPath("$.wellbeingLevel").value("GOOD"));
    }

    @Test
    @DisplayName("POST /api/v1/wellness/survey/submit returns 201 with recommendations")
    void submitSurvey_withRecommendations_includesThemInResponse() throws Exception {
        WellnessResource resource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Mental Health Center")
                .description("Counseling services")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Building A")
                .available(true)
                .build();

        when(submitSurveyUseCase.submitSurvey(any(SurveyResponse.class))).thenReturn(savedSurvey);
        when(recommendationsUseCase.getRecommendations(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(resource));

        mockMvc.perform(post("/api/v1/wellness/survey/submit")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recommendations[0].name").value("Mental Health Center"));
    }

    @Test
    @DisplayName("POST /api/v1/wellness/survey/submit returns 400 when scores out of range")
    void submitSurvey_invalidScore_returns400() throws Exception {
        SurveySubmissionRequest invalid = SurveySubmissionRequest.builder()
                .moodScore(6)
                .stressScore(3)
                .sleepScore(3)
                .socialScore(3)
                .academicScore(3)
                .build();

        mockMvc.perform(post("/api/v1/wellness/survey/submit")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/wellness/survey/history returns 200 with history")
    void getSurveyHistory_returns200WithList() throws Exception {
        when(submitSurveyUseCase.getSurveyHistory(userId)).thenReturn(List.of(savedSurvey));

        mockMvc.perform(get("/api/v1/wellness/survey/history")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].wellbeingLevel").value("GOOD"));
    }

    @Test
    @DisplayName("GET /api/v1/wellness/survey/history returns empty list when no surveys")
    void getSurveyHistory_noSurveys_returnsEmptyList() throws Exception {
        when(submitSurveyUseCase.getSurveyHistory(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/wellness/survey/history")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/wellness/survey/submit without auth returns 401")
    void submitSurvey_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/wellness/survey/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }
}
