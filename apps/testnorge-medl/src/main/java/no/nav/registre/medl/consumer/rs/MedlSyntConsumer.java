package no.nav.registre.medl.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.medl.consumer.rs.command.GetSyntMeldMeldingerCommand;
import no.nav.registre.medl.consumer.rs.credential.SyntMedlGcpProperties;
import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
@Slf4j
public class MedlSyntConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public MedlSyntConsumer(
            SyntMedlGcpProperties syntMedlGcpProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = syntMedlGcpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntMedlGcpProperties.getUrl())
                .build();
    }

    @Timed(value = "medl.resource.latency", extraTags = {"operation", "medl-syntetisereren"})
    public List<MedlSyntResponse> hentMedlemskapsmeldingerFromSyntRest(
            int numToGenerate
    ) {
        var accessToken = tokenExchange.generateToken(serviceProperties).block().getTokenValue();
        return new GetSyntMeldMeldingerCommand(numToGenerate, accessToken, webClient).call();
    }
}
