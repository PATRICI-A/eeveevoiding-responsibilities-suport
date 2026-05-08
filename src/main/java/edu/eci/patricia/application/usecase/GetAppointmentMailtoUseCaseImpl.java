package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.InvalidCategoryForMailtoException;
import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.AppointmentMailto;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetAppointmentMailtoUseCase;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class GetAppointmentMailtoUseCaseImpl implements GetAppointmentMailtoUseCase {

    private final WellnessResourceRepository repository;

    @Override
    public AppointmentMailto execute(String resourceId, String studentName) {

        WellnessResource resource = repository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));

        if (!resource.isMentalHealth()) {
            throw new InvalidCategoryForMailtoException(resourceId);
        }

        String subject = "Solicitud de cita - " + studentName;
        String body = "Estimada " + resource.getPsychologistName() + ",\n\n" +
                "Mi nombre es " + studentName + " y me gustaría agendar una cita de apoyo psicológico " +
                "con usted en el horario disponible.\n\n" +
                "Quedo atento/a a su confirmación.\n\n" +
                "Cordialmente,\n" + studentName;

        String mailtoLink = "mailto:" + resource.getAppointmentEmail() +
                "?subject=" + encode(subject) +
                "&body=" + encode(body);

        return AppointmentMailto.builder()
                .resourceId(resourceId)
                .psychologistName(resource.getPsychologistName())
                .appointmentEmail(resource.getAppointmentEmail())
                .subject(subject)
                .body(body)
                .mailtoLink(mailtoLink)
                .build();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}