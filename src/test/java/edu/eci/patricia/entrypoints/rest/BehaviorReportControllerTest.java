package edu.eci.patricia.entrypoints.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.eci.patricia.application.dto.BehaviorReportRequest;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.exception.WellnessException;
import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.model.ReportType;
import edu.eci.patricia.domain.ports.in.SubmitBehaviorReportUseCase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:reporttestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test-secret-for-ci-only-at-least-32-chars"
})
class BehaviorReportControllerTest {

    private static final String JWT_SECRET = "test-secret-for-ci-only-at-least-32-chars";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private SubmitBehaviorReportUseCase submitBehaviorReportUseCase;

    private String bearerToken;
    private UUID userId;
    private UUID reportId;
    private BehaviorReport sampleReport;
    private BehaviorReportRequest sampleRequest;

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

        reportId = UUID.randomUUID();
        sampleReport = BehaviorReport.builder()
                .id(reportId)
                .reporterId(userId)
                .description("A professor made offensive remarks during class")
                .reportType(ReportType.HARASSMENT)
                .status(ReportStatus.PENDING)
                .caseNumber("RPT-20260519-0042")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = BehaviorReportRequest.builder()
                .description("A professor made offensive remarks during class")
                .reportType(ReportType.HARASSMENT)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/wellness/reports returns 201 with caseNumber")
    void submitReport_valid_returns201WithCaseNumber() throws Exception {
        when(submitBehaviorReportUseCase.submitReport(any(BehaviorReport.class))).thenReturn(sampleReport);

        mockMvc.perform(post("/api/v1/wellness/reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("HARASSMENT"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.caseNumber").value("RPT-20260519-0042"))
                .andExpect(jsonPath("$.message").value("Tu reporte ha sido recibido. Número de caso: RPT-20260519-0042"));
    }

    @Test
    @DisplayName("POST returns 201 for INAPPROPRIATE_BEHAVIOR type")
    void submitReport_inappropriateBehavior_returns201() throws Exception {
        BehaviorReport inappropriateReport = BehaviorReport.builder()
                .id(UUID.randomUUID())
                .reporterId(userId)
                .description("Inappropriate behavior observed in hallway")
                .reportType(ReportType.INAPPROPRIATE_BEHAVIOR)
                .status(ReportStatus.PENDING)
                .caseNumber("RPT-20260519-0099")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        BehaviorReportRequest request = BehaviorReportRequest.builder()
                .description("Inappropriate behavior observed in hallway")
                .reportType(ReportType.INAPPROPRIATE_BEHAVIOR)
                .build();

        when(submitBehaviorReportUseCase.submitReport(any(BehaviorReport.class))).thenReturn(inappropriateReport);

        mockMvc.perform(post("/api/v1/wellness/reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("INAPPROPRIATE_BEHAVIOR"))
                .andExpect(jsonPath("$.caseNumber").value("RPT-20260519-0099"));
    }

    @Test
    @DisplayName("POST returns 201 with referenceId")
    void submitReport_withReferenceId_returns201() throws Exception {
        String refId = UUID.randomUUID().toString();
        BehaviorReportRequest requestWithRef = BehaviorReportRequest.builder()
                .description("Offensive content posted in event chat")
                .reportType(ReportType.OFFENSIVE_CONTENT)
                .referenceId(refId)
                .build();

        BehaviorReport reportWithRef = BehaviorReport.builder()
                .id(UUID.randomUUID())
                .reporterId(userId)
                .description("Offensive content posted in event chat")
                .reportType(ReportType.OFFENSIVE_CONTENT)
                .referenceId(refId)
                .status(ReportStatus.PENDING)
                .caseNumber("RPT-20260519-0111")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(submitBehaviorReportUseCase.submitReport(any(BehaviorReport.class))).thenReturn(reportWithRef);

        mockMvc.perform(post("/api/v1/wellness/reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithRef)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("OFFENSIVE_CONTENT"))
                .andExpect(jsonPath("$.referenceId").value(refId));
    }

    @Test
    @DisplayName("POST returns 400 when description missing")
    void submitReport_missingDescription_returns400() throws Exception {
        BehaviorReportRequest invalid = BehaviorReportRequest.builder()
                .reportType(ReportType.HARASSMENT)
                .build();

        mockMvc.perform(post("/api/v1/wellness/reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST returns 400 when reportType missing")
    void submitReport_missingReportType_returns400() throws Exception {
        BehaviorReportRequest invalid = BehaviorReportRequest.builder()
                .description("Some description")
                .build();

        mockMvc.perform(post("/api/v1/wellness/reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/wellness/reports/{id} returns 200 when reporter matches")
    void getReportById_ownReport_returns200() throws Exception {
        when(submitBehaviorReportUseCase.getReportById(reportId)).thenReturn(Optional.of(sampleReport));

        mockMvc.perform(get("/api/v1/wellness/reports/{id}", reportId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId.toString()));
    }

    @Test
    @DisplayName("GET /api/v1/wellness/reports/{id} returns 403 when requester is not reporter")
    void getReportById_differentUser_returns403() throws Exception {
        UUID otherUserId = UUID.randomUUID();
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String otherBearerToken = "Bearer " + Jwts.builder()
                .subject(otherUserId.toString())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        when(submitBehaviorReportUseCase.getReportById(reportId)).thenReturn(Optional.of(sampleReport));

        mockMvc.perform(get("/api/v1/wellness/reports/{id}", reportId)
                        .header(HttpHeaders.AUTHORIZATION, otherBearerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/wellness/reports/{id} returns 404 when report not found")
    void getReportById_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(submitBehaviorReportUseCase.getReportById(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/wellness/reports/{id}", unknownId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/wellness/reports/my-reports returns 200 with list")
    void getMyReports_returns200WithList() throws Exception {
        when(submitBehaviorReportUseCase.getReportsByReporter(userId)).thenReturn(List.of(sampleReport));

        mockMvc.perform(get("/api/v1/wellness/reports/my-reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportType").value("HARASSMENT"));
    }

    @Test
    @DisplayName("GET /api/v1/wellness/reports/my-reports returns empty list when none")
    void getMyReports_noReports_returnsEmptyList() throws Exception {
        when(submitBehaviorReportUseCase.getReportsByReporter(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/wellness/reports/my-reports")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/wellness/reports without auth returns 401")
    void submitReport_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/wellness/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isUnauthorized());
    }
}
