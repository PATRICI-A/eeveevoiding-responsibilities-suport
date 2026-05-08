package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.AppointmentMailto;

//Genera un enlace para solicitar una cita de psicologia
public interface GetAppointmentMailtoUseCase {
    AppointmentMailto execute(String resourceId, String studentName);
}