package edu.eci.patricia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain model representing a completed wellness survey submitted by a student (PTR23.1).
 * Stores the raw answers to questions P01–P10.
 * All ten questions are mandatory.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse {

    /** UUID generated automatically at submission time. */
    private String id;

    /** ID of the student who submitted the survey (extracted from JWT). */
    private String studentId;

    /**
     * Map of questionId → selected answer.
     * Keys: P01–P10. All 10 must be present.
     */
    private Map<String, String> answers;

    private LocalDateTime submittedAt;
}
