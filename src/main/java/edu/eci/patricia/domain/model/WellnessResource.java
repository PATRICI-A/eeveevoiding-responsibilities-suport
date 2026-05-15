package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.valueobjects.WellnessResourceId;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class WellnessResource {
    private final WellnessResourceId id;
    private final String name;
    private final String description;
    private final String contactInfo;
    private final String schedule;
    private final WellnessCategory category;
    private final boolean active;
    private final String appointmentEmail;
    private final String psychologistName;

    public boolean isMentalHealth() {
        return WellnessCategory.MENTAL_HEALTH.equals(this.category);
    }
}