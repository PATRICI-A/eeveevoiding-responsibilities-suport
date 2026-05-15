package edu.eci.patricia.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentMailtoResponse {
    private String appointmentEmailTo;
    private String appointmentEmailSubject;
    private String appointmentEmailBody;
}