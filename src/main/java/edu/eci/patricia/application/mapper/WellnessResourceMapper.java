package edu.eci.patricia.application.mapper;

import edu.eci.patricia.application.dto.request.CreateWellnessResourceRequest;
import edu.eci.patricia.application.dto.response.MailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.valueobjects.MailtoAppointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WellnessResourceMapper {

    @Mapping(target = "id", expression = "java(resource.getId().toString())")
    WellnessResourceResponse toResponse(WellnessResource resource);

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "active",           constant = "true")
    @Mapping(target = "psychologistName", source = "request.psychologistName")
    @Mapping(target = "appointmentEmail", source = "request.appointmentEmail")
    WellnessResource toDomain(CreateWellnessResourceRequest request);

    @Mapping(target = "mailtoUri", expression = "java(appointment.toMailtoUri())")
    @Mapping(target = "emailTo",   source = "appointment.emailTo")
    @Mapping(target = "subject",   source = "appointment.subject")
    @Mapping(target = "body",      source = "appointment.body")
    MailtoResponse toMailtoResponse(MailtoAppointment appointment);

    default String categoryToString(WellnessCategory category) {
        return category != null ? category.name() : null;
    }
}