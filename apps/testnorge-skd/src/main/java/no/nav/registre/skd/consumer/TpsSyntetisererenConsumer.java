package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.skd.consumer.command.GetSyntSkdMeldingerCommand;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;

@Component
@Slf4j
public class TpsSyntetisererenConsumer {

    private final StsOidcTokenService tokenService;
    private final WebClient webClient;

    public TpsSyntetisererenConsumer(
            StsOidcTokenService stsOidcTokenService,
            @Value("${synt-tps.rest.api.url}") String syntrestServerUrl
    ) {
        this.tokenService = stsOidcTokenService;
        this.webClient = WebClient.builder().baseUrl(syntrestServerUrl).build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tps-syntetisereren" })
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(
            String endringskode,
            Integer antallMeldinger
    ) {
        var response = new GetSyntSkdMeldingerCommand(endringskode, antallMeldinger, tokenService.getToken(), webClient).call();

        if (response != null && response.size() != antallMeldinger) {
            log.warn("Feil antall meldinger mottatt fra TPS-Syntetisereren. Forventet {}, men mottok {} meldinger.", antallMeldinger, response.size());
        }

        return response;
    }
}