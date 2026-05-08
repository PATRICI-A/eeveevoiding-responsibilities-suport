package edu.eci.patricia.application.mapper;

import edu.eci.patricia.application.dto.response.AppointmentMailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.domain.model.AppointmentMailto;
import edu.eci.patricia.domain.model.WellnessResource;
import org.springframework.stereotype.Component;

@Component
public class WellnessMapper {

    public WellnessResourceResponse toResponse(WellnessResource resource) {
        WellnessResourceResponse.WellnessResourceResponseBuilder builder =
                WellnessResourceResponse.builder()
                        .id(resource.getId())
                        .name(resource.getName())
                        .description(resource.getDescription())
                        .contactPhone(resource.getContactPhone())
                        .contactEmail(resource.getContactEmail())
                        .schedule(resource.getSchedule())
                        .category(resource.getCategory());

        //Solo nos dara los casos que tengan que ver con el Mental-Health
        if (resource.isMentalHealth()) {
            builder.appointmentEmail(resource.getAppointmentEmail())
                    .psychologistName(resource.getPsychologistName());
        }

        return builder.build();
    }

    public AppointmentMailtoResponse toMailtoResponse(AppointmentMailto mailto) {
        return AppointmentMailtoResponse.builder()
                .resourceId(mailto.getResourceId())
                .psychologistName(mailto.getPsychologistName())
                .appointmentEmail(mailto.getAppointmentEmail())
                .subject(mailto.getSubject())
                .body(mailto.getBody())
                .mailtoLink(mailto.getMailtoLink())
                .build();
    }
}