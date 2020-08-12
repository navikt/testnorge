package no.nav.registre.testnorge.rapportering.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.avro.report.Report;
import no.nav.registre.testnorge.rapportering.adapter.ReportAdapter;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class ReportListener {
    private final ReportAdapter adapter;

    @KafkaListener(topics = "testnorge-rapportering-v2")
    public void register(@Payload Report report) {
        log.info("Ny rapport registert");
        adapter.save(new no.nav.registre.testnorge.rapportering.domain.Report(report));
    }
}
