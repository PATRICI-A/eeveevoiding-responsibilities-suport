package edu.eci.patricia.application.dto.response;


import edu.eci.patricia.domain.model.enums.WellnessCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class WellnessResourceResponse {
    private UUID id;
    private String name;
    private String description;
    private String contactInfo;
    private String schedule;
    private WellnessCategory category;
    private boolean active;
    private boolean hasAppointment;
    private String appointmentEmail;
    private String psychologistName;
}