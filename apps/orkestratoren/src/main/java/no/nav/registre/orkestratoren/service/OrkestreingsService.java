package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.RapporteringConsumer;
import no.nav.registre.testnorge.libs.reporting.ReportingEngine;
import no.nav.registre.testnorge.libs.reporting.domin.Config;

@Service
@RequiredArgsConstructor
public class OrkestreingsService {

    private final SykemeldingOrkestreringsService sykemeldingOrkestreringsService;
    private final RapporteringConsumer rapporteringConsumer;

    public void orkisterSykemeldinger() {
        var reportingEngine = new ReportingEngine(
                Config.builder().name("Orkestrering av sykemeldinger batch").build(),
                sykemeldingOrkestreringsService::orkistrer,
                rapporteringConsumer
        );
        reportingEngine.run();
    }
}
