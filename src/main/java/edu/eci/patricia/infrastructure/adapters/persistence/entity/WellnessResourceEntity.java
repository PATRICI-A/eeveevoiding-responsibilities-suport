package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.WellnessCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity for the wellness_resources table (PTR23).
 */
@Entity
@Table(name = "wellness_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessResourceEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

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

    @Column(name = "appointment_email")
    private String appointmentEmail;

    @Column(name = "psychologist_name")
    private String psychologistName;
}