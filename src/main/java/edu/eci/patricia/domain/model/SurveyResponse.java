package edu.eci.patricia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a completed wellness survey submitted by a student.
 * Each dimension is scored 1-5 and an average wellbeing level is computed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse {

    /** Unique identifier for this survey submission. */
    private UUID id;

    /** Identifier of the student who submitted the survey. */
    private UUID userId;

    /** Self-reported mood score (1 = very bad, 5 = excellent). */
    private int moodScore;

    /** Self-reported stress score (1 = extremely stressed, 5 = relaxed). */
    private int stressScore;

    /** Self-reported sleep quality score (1 = very poor, 5 = excellent). */
    private int sleepScore;

    /** Self-reported social connection score (1 = isolated, 5 = very connected). */
    private int socialScore;

    /** Self-reported academic performance score (1 = struggling, 5 = thriving). */
    private int academicScore;

    /** Computed average of all five dimension scores. */
    private double averageScore;

    /** Derived wellbeing level based on the average score. */
    private WellbeingLevel wellbeingLevel;

    /** Timestamp when the survey was submitted. */
    private LocalDateTime submittedAt;
}
