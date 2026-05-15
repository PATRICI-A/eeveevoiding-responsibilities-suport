package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WellnessResourcePersistenceMapper {

    @Mapping(target = "id", expression = "java(edu.eci.patricia.domain.valueobjects.ResourceId.of(entity.getId()))")
    WellnessResource toDomain(WellnessResourceEntity entity);

    @Mapping(target = "id", expression = "java(resource.getId().value())")
    WellnessResourceEntity toEntity(WellnessResource resource);

    default ResourceId map(java.util.UUID value) {
        return ResourceId.of(value);
    }
}