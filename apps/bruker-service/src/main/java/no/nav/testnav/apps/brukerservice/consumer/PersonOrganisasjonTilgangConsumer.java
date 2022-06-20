package no.nav.testnav.apps.brukerservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.apps.brukerservice.consumer.command.GetOrganisasjonCommand;
import no.nav.testnav.apps.brukerservice.consumer.credentials.PersonOrganisasjonTilgangServiceProperties;
import no.nav.testnav.apps.brukerservice.domain.Organisasjon;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final PersonOrganisasjonTilgangServiceProperties properties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangConsumer(
            PersonOrganisasjonTilgangServiceProperties properties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.properties = properties;
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(properties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<Organisasjon> getOrganisasjon(String orgnummer) {
        return tokenExchange.exchange(properties)
                .flatMap(accessToken -> new GetOrganisasjonCommand(webClient, orgnummer, accessToken.getTokenValue()).call())
                .map(Organisasjon::new);
    }
}
