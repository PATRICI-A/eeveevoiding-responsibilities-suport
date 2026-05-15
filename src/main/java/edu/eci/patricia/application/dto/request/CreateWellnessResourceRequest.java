package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import lombok.Builder;

@Builder
public record CreateWellnessResourceRequest(
        String name,
        String description,
        String contact,
        String schedule,
        WellnessCategory category,
        String psychologistName,
        String appointmentEmail
) {}