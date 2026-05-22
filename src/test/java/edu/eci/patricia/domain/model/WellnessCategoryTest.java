package edu.eci.patricia.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WellnessCategoryTest {

    @Test
    void shouldHaveFourValues() {
        assertThat(WellnessCategory.values()).hasSize(4);
    }

    @Test
    void shouldContainAllExpectedCategories() {
        assertThat(WellnessCategory.valueOf("EMOTIONAL_SUPPORT")).isEqualTo(WellnessCategory.EMOTIONAL_SUPPORT);
        assertThat(WellnessCategory.valueOf("HEALTH")).isEqualTo(WellnessCategory.HEALTH);
        assertThat(WellnessCategory.valueOf("SPORTS")).isEqualTo(WellnessCategory.SPORTS);
        assertThat(WellnessCategory.valueOf("CULTURE")).isEqualTo(WellnessCategory.CULTURE);
    }
}
