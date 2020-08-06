package no.nav.registre.testnorge.libs.reporting;

import no.nav.registre.testnorge.libs.reporting.domin.Report;

@FunctionalInterface
public interface ReportConsumer {
    void send(Report report);
}