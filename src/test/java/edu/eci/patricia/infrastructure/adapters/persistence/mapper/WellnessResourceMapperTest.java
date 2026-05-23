package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WellnessResourceMapperTest {

    private WellnessResourceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WellnessResourceMapper();
    }

    @Test
    @DisplayName("toDomain converts entity to domain model correctly")
    void toDomain_validEntity_returnsDomainModel() {
        UUID id = UUID.randomUUID();

        WellnessResourceEntity entity = WellnessResourceEntity.builder()
                .id(id.toString())
                .name("Sports Center")
                .description("Gym and fitness classes")
                .category(WellnessCategory.SPORTS)
                .location("Sports Complex")
                .contactInfo("sports@eci.edu.co")
                .schedule("Mon-Sun 06:00-22:00")
                .available(true)
                .build();

        WellnessResource domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Sports Center");
        assertThat(domain.getDescription()).isEqualTo("Gym and fitness classes");
        assertThat(domain.getCategory()).isEqualTo(WellnessCategory.SPORTS);
        assertThat(domain.getLocation()).isEqualTo("Sports Complex");
        assertThat(domain.getContactInfo()).isEqualTo("sports@eci.edu.co");
        assertThat(domain.getSchedule()).isEqualTo("Mon-Sun 06:00-22:00");
        assertThat(domain.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("toDomain maps EMOTIONAL_SUPPORT resource with psychologist fields")
    void toDomain_emotionalSupportEntity_mapsPsychologistFields() {
        UUID id = UUID.randomUUID();

        WellnessResourceEntity entity = WellnessResourceEntity.builder()
                .id(id.toString())
                .name("Counseling Center")
                .description("Individual therapy")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .location("Building A")
                .available(true)
                .appointmentEmail("psicologia@eci.edu.co")
                .psychologistName("Dra. María García")
                .build();

        WellnessResource domain = mapper.toDomain(entity);

        assertThat(domain.getAppointmentEmail()).isEqualTo("psicologia@eci.edu.co");
        assertThat(domain.getPsychologistName()).isEqualTo("Dra. María García");
    }

    @Test
    @DisplayName("toDomain returns null when entity is null")
    void toDomain_nullEntity_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toEntity converts domain model to entity correctly")
    void toEntity_validDomain_returnsEntity() {
        UUID id = UUID.randomUUID();

        WellnessResource domain = WellnessResource.builder()
                .id(id)
                .name("Library")
                .description("Quiet study space")
                .category(WellnessCategory.CULTURE)
                .location("Main Campus")
                .contactInfo("library@eci.edu.co")
                .schedule("Mon-Fri 07:00-21:00")
                .available(false)
                .build();

        WellnessResourceEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id.toString());
        assertThat(entity.getName()).isEqualTo("Library");
        assertThat(entity.getDescription()).isEqualTo("Quiet study space");
        assertThat(entity.getCategory()).isEqualTo(WellnessCategory.CULTURE);
        assertThat(entity.getLocation()).isEqualTo("Main Campus");
        assertThat(entity.getContactInfo()).isEqualTo("library@eci.edu.co");
        assertThat(entity.getSchedule()).isEqualTo("Mon-Fri 07:00-21:00");
        assertThat(entity.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("toEntity generates UUID when domain id is null")
    void toEntity_nullDomainId_generatesUuid() {
        WellnessResource domain = WellnessResource.builder()
                .name("New Resource")
                .category(WellnessCategory.HEALTH)
                .available(true)
                .build();

        WellnessResourceEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isNotNull();
    }

    @Test
    @DisplayName("toEntity returns null when domain is null")
    void toEntity_nullDomain_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Round-trip preserves all fields")
    void roundTrip_entityToDomainToEntity_preservesFields() {
        UUID id = UUID.randomUUID();

        WellnessResourceEntity original = WellnessResourceEntity.builder()
                .id(id.toString())
                .name("Cultural Center")
                .description("Arts and cultural events")
                .category(WellnessCategory.CULTURE)
                .location("Student Union")
                .available(true)
                .build();

        WellnessResourceEntity roundTripped = mapper.toEntity(mapper.toDomain(original));

        assertThat(roundTripped.getId()).isEqualTo(original.getId());
        assertThat(roundTripped.getName()).isEqualTo(original.getName());
        assertThat(roundTripped.getCategory()).isEqualTo(WellnessCategory.CULTURE);
    }
}
