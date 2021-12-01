package no.nav.testnav.apps.brukerservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.brukerservice.client.command.GetOrganisasjonCommand;
import no.nav.testnav.apps.brukerservice.client.credentials.PersonOrganisasjonTilgangServiceProperties;
import no.nav.testnav.apps.brukerservice.domain.Organisasjon;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;

@Component
public class PersonOrganisasjonTilgangClient {
    private final WebClient webClient;
    private final PersonOrganisasjonTilgangServiceProperties properties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangClient(
            PersonOrganisasjonTilgangServiceProperties properties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(properties.getUrl())
                .build();
    }

    public Mono<Organisasjon> getOrganisasjon(String orgnummer) {
        return tokenExchange.exchange(properties)
                .flatMap(accessToken -> new GetOrganisasjonCommand(webClient, orgnummer, accessToken.getTokenValue()).call())
                .map(Organisasjon::new);
    }
}
