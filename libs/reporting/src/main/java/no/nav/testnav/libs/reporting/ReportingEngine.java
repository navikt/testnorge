package no.nav.testnav.libs.reporting;

import no.nav.testnav.libs.reporting.domin.Config;

public class ReportingEngine implements Runnable {
    private final Reportable reportable;
    private final Reporting reporting;
    private final ReportConsumer consumer;

    public ReportingEngine(Config config, Reportable reportable, ReportConsumer consumer) {
        this.reportable = reportable;
        this.consumer = consumer;
        this.reporting = new Reporting(config);
    }

    @Override
    public void run() {
        reporting.start();
        try {
            reportable.run(reporting);
        } catch (Exception e) {
            reporting.error("Uventet avsluttning på kjøringen: " + e.getMessage());
            throw e;
        } finally {
            reporting.end();
            consumer.send(reporting.build());
        }
    }
}