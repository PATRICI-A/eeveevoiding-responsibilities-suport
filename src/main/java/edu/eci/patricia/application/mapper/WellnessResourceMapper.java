package edu.eci.patricia.application.mapper;

import edu.eci.patricia.application.dto.request.CreateWellnessResourceRequest;
import edu.eci.patricia.application.dto.request.UpdateWellnessResourceRequest;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.valueobjects.WellnessResourceId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WellnessResourceMapper {

    @Mapping(target = "id", expression = "java(edu.eci.patricia.domain.valueobjects.WellnessResourceId.generate())")
    @Mapping(target = "active", constant = "true")
    WellnessResource toDomain(CreateWellnessResourceRequest request);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToValueObject")
    @Mapping(target = "active", constant = "true")
    WellnessResource toDomain(UUID id, UpdateWellnessResourceRequest request);

    @Mapping(target = "id", source = "id", qualifiedByName = "valueObjectToUuid")
    @Mapping(target = "hasAppointment", expression = "java(resource.isMentalHealth())")
    WellnessResourceResponse toResponse(WellnessResource resource);

    @Named("valueObjectToUuid")
    default UUID valueObjectToUuid(WellnessResourceId id) {
        return id.value();
    }

    @Named("uuidToValueObject")
    default WellnessResourceId uuidToValueObject(UUID id) {
        return WellnessResourceId.of(id);
    }
}