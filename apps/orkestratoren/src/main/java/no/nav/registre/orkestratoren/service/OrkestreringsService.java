package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.RapporteringConsumer;
import no.nav.registre.testnorge.libs.reporting.ReportingEngine;
import no.nav.registre.testnorge.libs.reporting.domin.Config;

@Service
@RequiredArgsConstructor
public class OrkestreringsService {

    private final SykemeldingOrkestreringsService sykemeldingOrkestreringsService;
    private final ArbeidsforholdOrkestreringsService arbeidsforholdOrkestreringsService;
    private final RapporteringConsumer rapporteringConsumer;

    public void orkestrerSykemeldinger() {
        var reportingEngine = new ReportingEngine(
                Config.builder().name("Orkestrering av sykemeldinger batch").build(),
                sykemeldingOrkestreringsService::orkistrer,
                rapporteringConsumer
        );
        reportingEngine.run();
    }

    public void orkestrerArbeidsforhold() {
        var reportingEngine = new ReportingEngine(
                Config.builder().name("Orkestrering av arbeidsforhold batch").build(),
                arbeidsforholdOrkestreringsService::orkistrer,
                rapporteringConsumer
        );
        reportingEngine.run();
    }
}
