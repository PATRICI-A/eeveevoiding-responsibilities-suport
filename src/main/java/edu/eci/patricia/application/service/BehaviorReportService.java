package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.model.ReportStatus;
import edu.eci.patricia.domain.ports.in.SubmitBehaviorReportUseCase;
import edu.eci.patricia.domain.ports.out.BehaviorReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing the behavior report submission and query use cases.
 * New reports are created with PENDING status and the current timestamp.
 */
@Service
@RequiredArgsConstructor
public class BehaviorReportService implements SubmitBehaviorReportUseCase {

    private final BehaviorReportRepositoryPort reportRepositoryPort;

    /**
     * {@inheritDoc}
     * Sets the initial status to PENDING and records the submission timestamp.
     */
    @Override
    public BehaviorReport submitReport(BehaviorReport report) {
        report.setStatus(ReportStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        return reportRepositoryPort.save(report);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BehaviorReport> getReportById(UUID id) {
        return reportRepositoryPort.findById(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no report exists with the given id
     */
    @Override
    public List<BehaviorReport> getReportsByReporter(UUID reporterId) {
        List<BehaviorReport> reports = reportRepositoryPort.findByReporterId(reporterId);
        return reports;
    }
}
