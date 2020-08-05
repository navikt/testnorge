package no.nav.registre.testnorge.rapportering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import no.nav.registre.testnorge.rapportering.adapter.ReportAdapter;
import no.nav.registre.testnorge.rapportering.consumer.SlackConsumer;
import no.nav.registre.testnorge.rapportering.domain.Report;

@Component
@RequiredArgsConstructor
public class ReportService {
    private final ReportAdapter adapter;
    private final SlackConsumer slackConsumer;

    public void publishAll() {
        List<Report> reports = adapter.findAll();
        for (Report report : reports) {
            slackConsumer.publish(report);
            adapter.delete(report.getId());
        }
    }
}
