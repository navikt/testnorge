package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer.command.GetOrgnummerCommand;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials.OrgnummerServiceProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrgnummerConsumer {

    private final WebClient webClient;
    private final OrgnummerServiceProperties properties;
    private final TokenExchange tokenExchange;

    public OrgnummerConsumer(OrgnummerServiceProperties properties,
                             TokenExchange tokenExchange,
                             ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public String getOrgnummer() {
        var tokenValue = tokenExchange.exchange(properties).block().getTokenValue();
        return new GetOrgnummerCommand(webClient, tokenValue, 1).call().stream().findFirst().orElseThrow();
    }

}