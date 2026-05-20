package edu.eci.patricia.application.service;

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
 * Application service implementing the behavior report use cases (RF24).
 * Generates a unique case number on submission and sets initial PENDING status.
 */
@Service
@RequiredArgsConstructor
public class BehaviorReportService implements SubmitBehaviorReportUseCase {

    private final BehaviorReportRepositoryPort reportRepositoryPort;
    private final Random random = new Random();

    /**
     * {@inheritDoc}
     * Sets status to PENDING, generates a case number in format RPT-YYYYMMDD-XXXX,
     * and records submission timestamp.
     */
    @Override
    public BehaviorReport submitReport(BehaviorReport report) {
        report.setStatus(ReportStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        report.setCaseNumber(generateCaseNumber(now));
        return reportRepositoryPort.save(report);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<BehaviorReport> getReportById(UUID id) {
        return reportRepositoryPort.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<BehaviorReport> getReportsByReporter(UUID reporterId) {
        return reportRepositoryPort.findByReporterId(reporterId);
    }

    /**
     * Generates a unique case number in format {@code RPT-YYYYMMDD-XXXX}.
     *
     * @param now the current timestamp used for the date portion
     * @return a formatted case number string
     */
    private String generateCaseNumber(LocalDateTime now) {
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = String.format("%04d", random.nextInt(10000));
        return "RPT-" + date + "-" + suffix;
    }
}
