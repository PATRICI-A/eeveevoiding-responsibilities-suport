package edu.eci.patricia.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ReportStatusTest {

    @Test
    void shouldHaveFourValues() {
        assertThat(ReportStatus.values()).hasSize(4);
    }

    @Test
    void shouldContainAllExpectedStatuses() {
        assertThat(ReportStatus.valueOf("PENDING")).isEqualTo(ReportStatus.PENDING);
        assertThat(ReportStatus.valueOf("UNDER_REVIEW")).isEqualTo(ReportStatus.UNDER_REVIEW);
        assertThat(ReportStatus.valueOf("RESOLVED")).isEqualTo(ReportStatus.RESOLVED);
        assertThat(ReportStatus.valueOf("DISMISSED")).isEqualTo(ReportStatus.DISMISSED);
    }
}
