package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.response.AppointmentMailtoResponse;

import java.util.UUID;

public interface GenerateAppointmentMailtoPort {
    AppointmentMailtoResponse generate(UUID id, String studentName);
}