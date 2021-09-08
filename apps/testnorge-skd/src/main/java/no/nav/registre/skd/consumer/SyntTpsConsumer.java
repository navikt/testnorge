package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.skd.consumer.command.GetSyntSkdMeldingerCommand;
import no.nav.registre.skd.consumer.credential.SyntTpsGcpProperties;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Component
@Slf4j
public class SyntTpsConsumer {

    private final AccessTokenService tokenService;
    private final NaisServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntTpsConsumer(
            SyntTpsGcpProperties syntTpsGcpProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntTpsGcpProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder().baseUrl(syntTpsGcpProperties.getUrl()).build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tps-syntetisereren" })
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(
            String endringskode,
            Integer antallMeldinger
    ) {
        var accessToken = tokenService.generateClientCredentialAccessToken(serviceProperties).getTokenValue();

        var response = new GetSyntSkdMeldingerCommand(endringskode, antallMeldinger, accessToken, webClient).call();

        if (response != null && response.size() != antallMeldinger) {
            log.warn("Feil antall meldinger mottatt fra TPS-Syntetisereren. Forventet {}, men mottok {} meldinger.", antallMeldinger, response.size());
        }

        return response;
    }
}