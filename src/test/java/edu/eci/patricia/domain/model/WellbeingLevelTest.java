package edu.eci.patricia.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WellbeingLevelTest {

    @Test
    void shouldHaveFiveValues() {
        assertThat(WellbeingLevel.values()).hasSize(5);
    }

    @Test
    void shouldContainAllExpectedLevels() {
        assertThat(WellbeingLevel.valueOf("CRITICAL")).isEqualTo(WellbeingLevel.CRITICAL);
        assertThat(WellbeingLevel.valueOf("LOW")).isEqualTo(WellbeingLevel.LOW);
        assertThat(WellbeingLevel.valueOf("MODERATE")).isEqualTo(WellbeingLevel.MODERATE);
        assertThat(WellbeingLevel.valueOf("GOOD")).isEqualTo(WellbeingLevel.GOOD);
        assertThat(WellbeingLevel.valueOf("EXCELLENT")).isEqualTo(WellbeingLevel.EXCELLENT);
    }
}
