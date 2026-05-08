package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WellnessResource {
    private String id;
    private String name;
    private String description;
    private String contactPhone;
    private String contactEmail;
    private String schedule;
    private WellnessCategory category;
    private boolean active;

    /** Solo presente si category == MENTAL_HEALTH */
    private String appointmentEmail;

    /** Solo presente si category == MENTAL_HEALTH */
    private String psychologistName;

    public boolean isMentalHealth() {
        return this.category == WellnessCategory.MENTAL_HEALTH;
    }
}