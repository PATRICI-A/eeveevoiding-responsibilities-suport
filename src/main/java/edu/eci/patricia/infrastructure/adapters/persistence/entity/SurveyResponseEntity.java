package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.WellbeingLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity mapping to the {@code survey_responses} table.
 * Stores a student's completed wellness survey with computed scores.
 */
@Entity
@Table(name = "survey_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseEntity {

    /** Primary key generated as a UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Identifier of the student who submitted the survey. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Self-reported mood score (1-5). */
    @Column(name = "mood_score", nullable = false)
    private int moodScore;

    /** Self-reported stress score (1-5). */
    @Column(name = "stress_score", nullable = false)
    private int stressScore;

    /** Self-reported sleep quality score (1-5). */
    @Column(name = "sleep_score", nullable = false)
    private int sleepScore;

    /** Self-reported social connection score (1-5). */
    @Column(name = "social_score", nullable = false)
    private int socialScore;

    /** Self-reported academic performance score (1-5). */
    @Column(name = "academic_score", nullable = false)
    private int academicScore;

    /** Computed average of all five scores. */
    @Column(name = "average_score", nullable = false)
    private double averageScore;

    /** Derived wellbeing level stored as a string. */
    @Enumerated(EnumType.STRING)
    @Column(name = "wellbeing_level", nullable = false)
    private WellbeingLevel wellbeingLevel;

    /** Timestamp automatically set when the record is inserted. */
    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
}
