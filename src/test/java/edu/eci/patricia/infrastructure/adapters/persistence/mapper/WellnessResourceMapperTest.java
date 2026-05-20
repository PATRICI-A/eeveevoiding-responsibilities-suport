package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link WellnessResourceMapper}.
 */
class WellnessResourceMapperTest {

    private WellnessResourceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WellnessResourceMapper();
    }

    @Test
    @DisplayName("toDomain converts entity to domain model correctly (SPORTS resource)")
    void toDomain_validEntity_returnsDomainModel() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        WellnessResourceEntity entity = WellnessResourceEntity.builder()
                .id(id)
                .name("Sports Center")
                .description("Gym and fitness classes")
                .category(WellnessCategory.SPORTS)
                .location("Sports Complex")
                .contactInfo("sports@eci.edu.co")
                .schedule("Mon-Sun 06:00-22:00")
                .available(true)
                .createdAt(now)
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
        assertThat(domain.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toDomain maps MENTAL_HEALTH resource with appointmentEmail and psychologistName")
    void toDomain_mentalHealthEntity_mapsPsychologistFields() {
        UUID id = UUID.randomUUID();

        WellnessResourceEntity entity = WellnessResourceEntity.builder()
                .id(id)
                .name("Counseling Center")
                .description("Individual therapy")
                .category(WellnessCategory.MENTAL_HEALTH)
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
    @DisplayName("toEntity converts domain model to entity correctly (ACADEMIC_SUPPORT resource)")
    void toEntity_validDomain_returnsEntity() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        WellnessResource domain = WellnessResource.builder()
                .id(id)
                .name("Library")
                .description("Academic support and quiet study space")
                .category(WellnessCategory.ACADEMIC_SUPPORT)
                .location("Main Campus")
                .contactInfo("library@eci.edu.co")
                .schedule("Mon-Fri 07:00-21:00")
                .available(false)
                .createdAt(now)
                .build();

        WellnessResourceEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Library");
        assertThat(entity.getDescription()).isEqualTo("Academic support and quiet study space");
        assertThat(entity.getCategory()).isEqualTo(WellnessCategory.ACADEMIC_SUPPORT);
        assertThat(entity.getLocation()).isEqualTo("Main Campus");
        assertThat(entity.getContactInfo()).isEqualTo("library@eci.edu.co");
        assertThat(entity.getSchedule()).isEqualTo("Mon-Fri 07:00-21:00");
        assertThat(entity.isAvailable()).isFalse();
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toEntity returns null when domain is null")
    void toEntity_nullDomain_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Round-trip entity→domain→entity preserves all fields including new categories")
    void roundTrip_entityToDomainToEntity_preservesFields() {
        UUID id = UUID.randomUUID();

        WellnessResourceEntity original = WellnessResourceEntity.builder()
                .id(id)
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
