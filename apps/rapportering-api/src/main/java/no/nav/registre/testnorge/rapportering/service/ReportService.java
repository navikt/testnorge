package no.nav.registre.testnorge.rapportering.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import no.nav.registre.testnorge.rapportering.adapter.ReportAdapter;
import no.nav.registre.testnorge.rapportering.consumer.SlackConsumer;
import no.nav.registre.testnorge.rapportering.domain.Report;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportService {
    private final ReportAdapter adapter;
    private final SlackConsumer slackConsumer;

    public void publishAll() {
        List<Report> reports = adapter.findAll();
        log.info("Fant {} rapportert å publisere.", reports.size());
        for (Report report : reports) {
            slackConsumer.publish(report);
            adapter.delete(report.getId());
        }
    }
}
