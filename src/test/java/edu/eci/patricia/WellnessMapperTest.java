package edu.eci.patricia;


import edu.eci.patricia.application.dto.response.AppointmentMailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.application.mapper.WellnessMapper;
import edu.eci.patricia.domain.model.AppointmentMailto;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WellnessMapperTest {

    private WellnessMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WellnessMapper();
    }

    @Test
    void toResponse_mentalHealth_incluyeCamposDeCita() {
        WellnessResource resource = WellnessResource.builder()
                .id("1").name("Psicología").description("Apoyo")
                .contactPhone("601-123").contactEmail("bio@eci.edu.co")
                .schedule("L-V").category(WellnessCategory.MENTAL_HEALTH).active(true)
                .appointmentEmail("psi@eci.edu.co").psychologistName("Dra. Andrea Gómez").build();

        WellnessResourceResponse response = mapper.toResponse(resource);

        assertThat(response.getAppointmentEmail()).isEqualTo("psi@eci.edu.co");
        assertThat(response.getPsychologistName()).isEqualTo("Dra. Andrea Gómez");
    }

    @Test
    void toResponse_noMentalHealth_noCamposDeCita() {
        WellnessResource resource = WellnessResource.builder()
                .id("2").name("Canchas").description("Canchas")
                .contactPhone("601-456").contactEmail("dep@eci.edu.co")
                .schedule("L-S").category(WellnessCategory.SPORTS).active(true).build();

        WellnessResourceResponse response = mapper.toResponse(resource);

        assertThat(response.getAppointmentEmail()).isNull();
        assertThat(response.getPsychologistName()).isNull();
    }

    @Test
    void toMailtoResponse_mapeaTodosLosCampos() {
        AppointmentMailto mailto = AppointmentMailto.builder()
                .resourceId("1").psychologistName("Dra. Andrea Gómez")
                .appointmentEmail("psi@eci.edu.co")
                .subject("Solicitud de cita - Laura González")
                .body("Estimada Dra. Andrea Gómez...")
                .mailtoLink("mailto:psi@eci.edu.co?subject=...&body=...").build();

        AppointmentMailtoResponse response = mapper.toMailtoResponse(mailto);

        assertThat(response.getResourceId()).isEqualTo("1");
        assertThat(response.getMailtoLink()).startsWith("mailto:");
        assertThat(response.getSubject()).contains("Laura González");
    }
}