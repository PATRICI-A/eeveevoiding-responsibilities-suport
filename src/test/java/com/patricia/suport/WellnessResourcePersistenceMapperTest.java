package com.patricia.suport;


import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourcePersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WellnessResourcePersistenceMapperTest {

    private WellnessResourcePersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WellnessResourcePersistenceMapper();
    }

    @Test
    void toDomain_mapeaTodosLosCampos() {
        WellnessResourceEntity entity = WellnessResourceEntity.builder()
                .id("1").name("Psicología").description("Apoyo psicológico")
                .contactPhone("601-123").contactEmail("bio@eci.edu.co")
                .schedule("L-V 8-17").category(WellnessCategory.MENTAL_HEALTH)
                .active(true).appointmentEmail("psi@eci.edu.co")
                .psychologistName("Dra. Andrea Gómez").build();

        WellnessResource domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo("1");
        assertThat(domain.getCategory()).isEqualTo(WellnessCategory.MENTAL_HEALTH);
        assertThat(domain.isActive()).isTrue();
        assertThat(domain.getAppointmentEmail()).isEqualTo("psi@eci.edu.co");
        assertThat(domain.getPsychologistName()).isEqualTo("Dra. Andrea Gómez");
    }

    @Test
    void toEntity_mapeaTodosLosCampos() {
        WellnessResource domain = WellnessResource.builder()
                .id("2").name("Canchas").description("Canchas de fútbol")
                .contactPhone("601-456").contactEmail("dep@eci.edu.co")
                .schedule("L-S 6-22").category(WellnessCategory.SPORTS)
                .active(true).build();

        WellnessResourceEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo("2");
        assertThat(entity.getCategory()).isEqualTo(WellnessCategory.SPORTS);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getAppointmentEmail()).isNull();
        assertThat(entity.getPsychologistName()).isNull();
    }

    @Test
    void toDomain_toEntity_roundTrip() {
        WellnessResourceEntity original = WellnessResourceEntity.builder()
                .id("3").name("Gimnasio").description("Gym")
                .contactPhone("601-789").contactEmail("dep@eci.edu.co")
                .schedule("L-V 6-21").category(WellnessCategory.SPORTS)
                .active(true).build();

        WellnessResource domain = mapper.toDomain(original);
        WellnessResourceEntity roundTripped = mapper.toEntity(domain);

        assertThat(roundTripped.getId()).isEqualTo(original.getId());
        assertThat(roundTripped.getCategory()).isEqualTo(original.getCategory());
        assertThat(roundTripped.isActive()).isEqualTo(original.isActive());
    }
}
