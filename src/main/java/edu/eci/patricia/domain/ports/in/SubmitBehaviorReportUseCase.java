package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.BehaviorReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for submitting and querying inappropriate behavior reports.
 */
public interface SubmitBehaviorReportUseCase {

    /**
     * Stores a new behavior report submitted by a student.
     *
     * @param report the report data (id may be null — will be assigned)
     * @return the persisted report with generated id and timestamps
     */
    BehaviorReport submitReport(BehaviorReport report);

    /**
     * Returns a behavior report by its identifier.
     *
     * @param id the UUID of the report to retrieve
     * @return an Optional containing the report, or empty if not found
     */
    Optional<BehaviorReport> getReportById(UUID id);

    /**
     * Returns all behavior reports submitted by a specific student.
     *
     * @param reporterId the UUID of the student whose reports to retrieve
     * @return list of reports submitted by that student
     */
    List<BehaviorReport> getReportsByReporter(UUID reporterId);
}
