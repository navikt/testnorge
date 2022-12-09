package no.nav.registre.tp.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.consumer.command.GetLevendeIdenterCommand;
import no.nav.registre.tp.consumer.credential.HodejegerenProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
@Slf4j
public class HodejegerenConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public HodejegerenConsumer(HodejegerenProperties serviceProperties,
                               TokenExchange tokenExchange,
                               ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> getLevende(
            Long avspillergruppeId
    ) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetLevendeIdenterCommand(avspillergruppeId, accessToken.getTokenValue(), webClient).call())
                .block();
    }

}
