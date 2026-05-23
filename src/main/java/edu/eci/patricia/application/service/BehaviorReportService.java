package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.BehaviorReportRequest;
import edu.eci.patricia.application.dto.BehaviorReportResponse;
import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.ports.in.SubmitBehaviorReportUseCase;
import edu.eci.patricia.domain.ports.out.BehaviorReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Application service implementing behavior report use cases (PTR24).
 * Generates caseNumber in format RPT-YYYYMMDD-XXXX (RN-24.1).
 * Sets initial status PENDING (RN-24.2).
 * reporterId always comes from JWT, never from the client (RN-24.3).
 */
@Service
@RequiredArgsConstructor
public class BehaviorReportService implements SubmitBehaviorReportUseCase {

    private final BehaviorReportRepositoryPort reportRepositoryPort;
    private final Random random = new Random();

    /**
     * Submits a new behavior report (PTR24).
     *
     * @param reporterId extracted from JWT — never from request body (RN-24.3)
     * @param request    behavior report data
     * @return response with caseNumber always present in message (RN-24.1)
     */
    public BehaviorReportResponse submitReport(String reporterId, BehaviorReportRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String caseNumber = generateCaseNumber(now);

        BehaviorReport report = BehaviorReport.builder()
                .id(UUID.randomUUID())
                .reporterId(UUID.fromString(reporterId.contains("-") ? reporterId :
                        "00000000-0000-0000-0000-" + String.format("%012d", reporterId.hashCode() & 0xFFFFFFFFFFFFL)))
                .reportType(request.getReportType())
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .status(ReportStatus.PENDING)
                .caseNumber(caseNumber)
                .createdAt(now)
                .updatedAt(now)
                .build();

        reportRepositoryPort.save(report);

        return BehaviorReportResponse.builder()
                .id(report.getId())
                .caseNumber(caseNumber)
                .message("Tu reporte ha sido recibido. Número de caso: " + caseNumber)
                .status(ReportStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Override
    public BehaviorReport submitReport(BehaviorReport report) {
        report.setStatus(ReportStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        report.setCaseNumber(generateCaseNumber(now));
        return reportRepositoryPort.save(report);
    }

    @Override
    public Optional<BehaviorReport> getReportById(UUID id) {
        return reportRepositoryPort.findById(id);
    }

    @Override
    public List<BehaviorReport> getReportsByReporter(UUID reporterId) {
        return reportRepositoryPort.findByReporterId(reporterId);
    }

    private String generateCaseNumber(LocalDateTime now) {
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = String.format("%04d", random.nextInt(10000));
        return "RPT-" + date + "-" + suffix;
    }
}