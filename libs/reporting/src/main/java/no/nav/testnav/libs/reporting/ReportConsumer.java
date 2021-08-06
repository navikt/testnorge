package no.nav.testnav.libs.reporting;

import no.nav.testnav.libs.reporting.domin.Report;

@FunctionalInterface
public interface ReportConsumer {
    void send(Report report);
}