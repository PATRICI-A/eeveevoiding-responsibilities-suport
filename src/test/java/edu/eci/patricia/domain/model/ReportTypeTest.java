package edu.eci.patricia.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ReportTypeTest {

    @Test
    void shouldHaveThreeValues() {
        assertThat(ReportType.values()).hasSize(3);
    }

    @Test
    void shouldContainAllExpectedTypes() {
        assertThat(ReportType.valueOf("HARASSMENT")).isEqualTo(ReportType.HARASSMENT);
        assertThat(ReportType.valueOf("INAPPROPRIATE_BEHAVIOR")).isEqualTo(ReportType.INAPPROPRIATE_BEHAVIOR);
        assertThat(ReportType.valueOf("OFFENSIVE_CONTENT")).isEqualTo(ReportType.OFFENSIVE_CONTENT);
    }
}
