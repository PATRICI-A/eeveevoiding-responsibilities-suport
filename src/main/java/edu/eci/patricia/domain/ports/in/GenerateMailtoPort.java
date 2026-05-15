package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.valueobjects.MailtoAppointment;
import edu.eci.patricia.domain.valueobjects.ResourceId;

public interface GenerateMailtoPort {
    MailtoAppointment generate(ResourceId id, String studentName);
}