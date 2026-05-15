package edu.eci.patricia.application.dto.request;


import edu.eci.patricia.domain.model.enums.WellnessCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateWellnessResourceRequest {
    private String name;
    private String description;
    private String contactInfo;
    private String schedule;
    private WellnessCategory category;
    private String appointmentEmail;
    private String psychologistName;
}