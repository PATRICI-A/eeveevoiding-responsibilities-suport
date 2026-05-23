package edu.eci.patricia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Domain model representing a wellness resource available to students (PTR23).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessResource {

    private UUID id;
    private String name;
    private String description;
    private WellnessCategory category;
    private String location;
    private String contactInfo;
    private String schedule;
    private boolean available;

    /** Psychologist email — only for EMOTIONAL_SUPPORT resources. */
    private String appointmentEmail;

    /** Psychologist name — only for EMOTIONAL_SUPPORT resources. */
    private String psychologistName;

    public boolean isEmotionalSupport() {
        return this.category == WellnessCategory.EMOTIONAL_SUPPORT;
    }
}
//EMOTIONAL_SUPPORT