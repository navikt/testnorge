package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.skd.consumer.command.GetSyntSkdMeldingerCommand;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
@Slf4j
public class TpsSyntetisererenConsumer {

    private final WebClient webClient;

    public TpsSyntetisererenConsumer(
            @Value("${syntrest.rest.api.url}") String syntrestServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(syntrestServerUrl).build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tps-syntetisereren" })
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(
            String endringskode,
            Integer antallMeldinger
    ) {
        var response = new GetSyntSkdMeldingerCommand(endringskode, antallMeldinger, webClient).call();

        if (response != null && response.size() != antallMeldinger) {
            log.warn("Feil antall meldinger mottatt fra TPS-Syntetisereren. Forventet {}, men mottok {} meldinger.", antallMeldinger, response.size());
        }

        return response;
    }
}