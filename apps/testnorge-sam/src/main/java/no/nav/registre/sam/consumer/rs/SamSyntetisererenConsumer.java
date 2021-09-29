package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sam.consumer.rs.command.GetSyntSamMeldingerCommand;
import no.nav.registre.sam.consumer.rs.credential.SyntSamGcpProperties;
import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class SamSyntetisererenConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SamSyntetisererenConsumer(
            SyntSamGcpProperties syntSamGcpProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntSamGcpProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntSamGcpProperties.getUrl())
                .build();
    }

    @Timed(value = "sam.resource.latency", extraTags = {"operation", "sam-syntetisereren"})
    public List<SyntetisertSamordningsmelding> hentSammeldingerFromSyntRest(
            int numToGenerate
    ) {
        List<SyntetisertSamordningsmelding> syntetiserteMeldinger = new ArrayList<>();

        var token = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        var response = new GetSyntSamMeldingerCommand(numToGenerate, token, webClient).call();
        if (response != null) {
            syntetiserteMeldinger.addAll(response);
        } else {
            log.error("Kunne ikke hente response body fra synthdata-sam: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
