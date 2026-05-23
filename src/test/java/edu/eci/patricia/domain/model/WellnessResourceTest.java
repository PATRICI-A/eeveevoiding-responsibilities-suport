package edu.eci.patricia.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class WellnessResourceTest {

    @Test
    void isEmotionalSupport_shouldReturnTrueWhenCategoryIsEMOTIONAL_SUPPORT() {
        WellnessResource resource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Psicología")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .build();
        assertThat(resource.isEmotionalSupport()).isTrue();
    }

    @Test
    void isEmotionalSupport_shouldReturnFalseForNonEmotionalSupport() {
        WellnessResource resource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Gimnasio")
                .category(WellnessCategory.SPORTS)
                .build();
        assertThat(resource.isEmotionalSupport()).isFalse();
    }

    @Test
    void builderShouldSetAllFields() {
        UUID id = UUID.randomUUID();
        WellnessResource resource = WellnessResource.builder()
                .id(id)
                .name("Test Resource")
                .description("A test resource")
                .category(WellnessCategory.HEALTH)
                .location("Building A")
                .contactInfo("test@test.com")
                .schedule("Mon-Fri 9-5")
                .available(true)
                .appointmentEmail("appt@test.com")
                .psychologistName("Dr. Test")
                .build();

        assertThat(resource.getId()).isEqualTo(id);
        assertThat(resource.getName()).isEqualTo("Test Resource");
        assertThat(resource.getDescription()).isEqualTo("A test resource");
        assertThat(resource.getCategory()).isEqualTo(WellnessCategory.HEALTH);
        assertThat(resource.getLocation()).isEqualTo("Building A");
        assertThat(resource.getContactInfo()).isEqualTo("test@test.com");
        assertThat(resource.getSchedule()).isEqualTo("Mon-Fri 9-5");
        assertThat(resource.isAvailable()).isTrue();
        assertThat(resource.getAppointmentEmail()).isEqualTo("appt@test.com");
        assertThat(resource.getPsychologistName()).isEqualTo("Dr. Test");
    }
}
