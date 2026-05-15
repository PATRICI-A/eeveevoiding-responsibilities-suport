package edu.eci.patricia.application.dto.request;

import lombok.Builder;

@Builder
public record UpdateWellnessResourceRequest(
        String name,
        String description,
        String contact,
        String schedule,
        String psychologistName,
        String appointmentEmail
) {}