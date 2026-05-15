package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "wellness_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WellnessResourceEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;

    @Column(name = "schedule", nullable = false)
    private String schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private WellnessCategory category;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "appointment_email")
    private String appointmentEmail;

    @Column(name = "psychologist_name")
    private String psychologistName;
}