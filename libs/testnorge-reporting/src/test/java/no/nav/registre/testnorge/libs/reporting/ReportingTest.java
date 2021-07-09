package no.nav.registre.testnorge.libs.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.testnav.libs.dto.rapprtering.v1.EntryStatus;
import no.nav.registre.testnorge.libs.reporting.domin.Config;
import no.nav.registre.testnorge.libs.reporting.domin.Entry;

class ReportingTest {

    private Reporting reporting;

    @BeforeEach
    void before() {
        reporting = new Reporting(Config.builder().name("name").build());
    }

    @Test
    void should_report_info_warn_and_error() {
        reporting.info("Dette er en test av info");
        reporting.warn("Dette er en test av warn");
        reporting.error("Dette er en test av error");

        assertThat(reporting.build().getEntries())
                .usingElementComparatorIgnoringFields("time")
                .containsExactly(
                        new Entry(EntryStatus.INFO, "Dette er en test av info", null),
                        new Entry(EntryStatus.WARNING, "Dette er en test av warn", null),
                        new Entry(EntryStatus.ERROR, "Dette er en test av error", null)
                );
    }

    @Test
    void should_report_info_with_injected_values() {
        reporting.info("Dette er første: {} andre: {} og tredje: {}", 1.0, "test", (float) 100);
        assertThat(reporting.build().getEntries())
                .usingElementComparatorIgnoringFields("time")
                .containsOnly(
                        new Entry(EntryStatus.INFO, "Dette er første: 1.0 andre: test og tredje: 100.0", null)
                );
    }

    @Test
    void should_handle_characters(){
        reporting.info("Dette er test av: {}", "$");
        assertThat(reporting.build().getEntries())
                .usingElementComparatorIgnoringFields("time")
                .containsOnly(
                        new Entry(EntryStatus.INFO, "Dette er test av: $", null)
                );
    }

}