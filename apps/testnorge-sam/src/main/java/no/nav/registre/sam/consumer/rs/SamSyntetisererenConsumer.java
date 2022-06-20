package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sam.consumer.rs.command.GetSyntSamMeldingerCommand;
import no.nav.registre.sam.consumer.rs.credential.SyntSamGcpProperties;
import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SamSyntetisererenConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SamSyntetisererenConsumer(
            SyntSamGcpProperties syntSamGcpProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = syntSamGcpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntSamGcpProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "sam.resource.latency", extraTags = {"operation", "sam-syntetisereren"})
    public List<SyntetisertSamordningsmelding> hentSammeldingerFromSyntRest(
            int numToGenerate
    ) {
        List<SyntetisertSamordningsmelding> syntetiserteMeldinger = new ArrayList<>();

        var token = tokenExchange.exchange(serviceProperties).block().getTokenValue();
        var response = new GetSyntSamMeldingerCommand(numToGenerate, token, webClient).call();
        if (response != null && !response.isEmpty()) {
            syntetiserteMeldinger.addAll(response);
        } else {
            log.error("Klarte ikke hente sam-meldinger fra synt-sam-gcp.");
        }

        return syntetiserteMeldinger;
    }
}
