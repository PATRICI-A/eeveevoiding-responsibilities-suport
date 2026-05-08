package edu.eci.patricia.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentMailto {
    private String resourceId;
    private String psychologistName;
    private String appointmentEmail;
    private String subject;
    private String body;
    private String mailtoLink;
}
