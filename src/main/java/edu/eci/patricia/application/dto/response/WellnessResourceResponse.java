package edu.eci.patricia.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WellnessResourceResponse(
        String id,
        String name,
        String description,
        String contact,
        String schedule,
        WellnessCategory category,
        boolean active,
        String psychologistName,
        String appointmentEmail
) {}