package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.BehaviorReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port defining persistence operations for behavior reports.
 */
public interface BehaviorReportRepositoryPort {

    /**
     * Persists a behavior report.
     *
     * @param report the report to save
     * @return the saved report
     */
    BehaviorReport save(BehaviorReport report);

    /**
     * Retrieves a behavior report by its unique identifier.
     *
     * @param id the UUID of the report
     * @return an Optional containing the report, or empty if not found
     */
    Optional<BehaviorReport> findById(UUID id);

    /**
     * Retrieves all behavior reports submitted by a specific student.
     *
     * @param reporterId the UUID of the reporter
     * @return list of reports submitted by that student
     */
    List<BehaviorReport> findByReporterId(UUID reporterId);
}
