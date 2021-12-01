package no.nav.registre.frikort.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import no.nav.registre.frikort.consumer.rs.command.PostSyntFrikortMeldingerCommand;
import no.nav.registre.frikort.consumer.rs.credential.SyntFrikortGcpProperties;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
@Slf4j
public class SyntFrikortConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntFrikortConsumer(
            SyntFrikortGcpProperties syntFrikortGcpProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = syntFrikortGcpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntFrikortGcpProperties.getUrl())
                .build();
    }

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeEgenandelerFraSyntRest(Map<String, Integer> request) {
        try {
            var accessToken = tokenExchange.generateToken(serviceProperties).block().getTokenValue();
            return new PostSyntFrikortMeldingerCommand(request, accessToken, webClient).call();
        } catch (Exception e) {
            log.error("Uventet feil ved henting av syntetiske egenandeler fra synth-frikort-gcp.", e);
            throw e;
        }
    }
}