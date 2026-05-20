package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.WellnessCategory;
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
 * JPA entity mapping to the {@code wellness_resources} table (RF23).
 * For MENTAL_HEALTH resources, appointmentEmail and psychologistName are populated.
 */
@Entity
@Table(name = "wellness_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WellnessCategory category;

    @Column(nullable = false)
    private String location;

    private String contactInfo;

    private String schedule;

    @Column(nullable = false)
    private boolean available;

    /** Psychologist's email address — only for MENTAL_HEALTH resources. */
    @Column(name = "appointment_email")
    private String appointmentEmail;

    /** Psychologist's name — only for MENTAL_HEALTH resources. */
    @Column(name = "psychologist_name")
    private String psychologistName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
