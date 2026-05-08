package edu.eci.patricia;

import edu.eci.patricia.domain.model.enums.EventStatus;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumsTest {

    // ── EventStatus ──────────────────────────────────────────────────────────

    @Test
    void eventStatus_tieneLosSeisPosiblesValores() {
        EventStatus[] values = EventStatus.values();
        assertThat(values).hasSize(6);
    }

    @Test
    void eventStatus_valueOf_retornaElValorCorrecto() {
        assertThat(EventStatus.valueOf("ACTIVE")).isEqualTo(EventStatus.ACTIVE);
        assertThat(EventStatus.valueOf("INACTIVE")).isEqualTo(EventStatus.INACTIVE);
        assertThat(EventStatus.valueOf("CANCELLED")).isEqualTo(EventStatus.CANCELLED);
        assertThat(EventStatus.valueOf("COMPLETED")).isEqualTo(EventStatus.COMPLETED);
        assertThat(EventStatus.valueOf("PENDING")).isEqualTo(EventStatus.PENDING);
        assertThat(EventStatus.valueOf("IN_PROGRESS")).isEqualTo(EventStatus.IN_PROGRESS);
    }

    @Test
    void eventStatus_name_retornaNombreExacto() {
        assertThat(EventStatus.ACTIVE.name()).isEqualTo("ACTIVE");
        assertThat(EventStatus.IN_PROGRESS.name()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void eventStatus_ordinal_esConsistente() {
        assertThat(EventStatus.ACTIVE.ordinal()).isEqualTo(0);
        assertThat(EventStatus.INACTIVE.ordinal()).isEqualTo(1);
    }

    @Test
    void eventStatus_valorInvalido_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> EventStatus.valueOf("NO_EXISTE"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── WellnessCategory ─────────────────────────────────────────────────────

    @Test
    void wellnessCategory_tieneLasCuatroCategorias() {
        assertThat(WellnessCategory.values()).hasSize(4);
    }

    @Test
    void wellnessCategory_valueOf_retornaElValorCorrecto() {
        assertThat(WellnessCategory.valueOf("MENTAL_HEALTH")).isEqualTo(WellnessCategory.MENTAL_HEALTH);
        assertThat(WellnessCategory.valueOf("SPORTS")).isEqualTo(WellnessCategory.SPORTS);
        assertThat(WellnessCategory.valueOf("CULTURE")).isEqualTo(WellnessCategory.CULTURE);
        assertThat(WellnessCategory.valueOf("ACADEMIC_SUPPORT")).isEqualTo(WellnessCategory.ACADEMIC_SUPPORT);
    }
}