package no.nav.registre.testnorge.rapportering.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;
import no.nav.registre.testnorge.rapportering.adapter.ReportAdapter;
import no.nav.registre.testnorge.rapportering.domain.Report;

@Slf4j
@Component
public class ReportService {
    private final ReportAdapter adapter;
    private final SlackConsumer slackConsumer;
    private final String channel;

    public ReportService(
            ReportAdapter adapter,
            SlackConsumer slackConsumer,
            @Value("${consumer.slack.channel}") String channel
    ) {
        this.adapter = adapter;
        this.slackConsumer = slackConsumer;
        this.channel = channel;
    }

    public void publishAll() {
        List<Report> reports = adapter.findAll();
        log.info("Fant {} rapportert å publisere.", reports.size());
        for (Report report : reports) {
            slackConsumer.publish(report.toSlackMessage(channel));
            adapter.delete(report.getId());
        }
    }

    public void deleteAll() {
        log.warn("Sletter alle rapporter...");
        adapter.deleteAll();
    }
}
