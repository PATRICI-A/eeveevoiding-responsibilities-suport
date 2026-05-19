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
 * JPA entity mapping to the {@code wellness_resources} table.
 * Represents a campus wellness resource persisted in the database.
 */
@Entity
@Table(name = "wellness_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessResourceEntity {

    /** Primary key generated as a UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Display name of the resource. */
    @Column(nullable = false)
    private String name;

    /** Detailed description of the services offered. */
    @Column(nullable = false, length = 1000)
    private String description;

    /** Wellness category stored as a string. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WellnessCategory category;

    /** Physical or virtual location. */
    @Column(nullable = false)
    private String location;

    /** Contact email, phone, or website. */
    private String contactInfo;

    /** Operating schedule. */
    private String schedule;

    /** Whether the resource is currently available. */
    @Column(nullable = false)
    private boolean available;

    /** Timestamp automatically set on insert. */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
