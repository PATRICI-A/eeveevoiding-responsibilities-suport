package edu.eci.patricia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a wellness resource available to students on campus.
 * Resources can include counselors, health services, cafeterias, libraries, etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessResource {

    /** Unique identifier of the resource. */
    private UUID id;

    /** Display name of the wellness resource. */
    private String name;

    /** Detailed description of the services provided. */
    private String description;

    /** Category that classifies this resource. */
    private WellnessCategory category;

    /** Physical or virtual location of the resource. */
    private String location;

    /** Contact information such as email, phone, or website. */
    private String contactInfo;

    /** Operating schedule (e.g., "Mon-Fri 08:00-17:00"). */
    private String schedule;

    /** Whether the resource is currently available for student use. */
    private boolean available;

    /** Timestamp when the resource was created in the system. */
    private LocalDateTime createdAt;
}
