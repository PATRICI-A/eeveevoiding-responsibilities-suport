package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * JPA entity for the survey_responses table (PTR23.1).
 * Stores raw P01–P10 answers as a JSONB column.
 */
@Entity
@Table(name = "survey_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    /**
     * Raw answers stored as JSON: {"P01":"Bien", "P02":"3", ...}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> answers;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
}
