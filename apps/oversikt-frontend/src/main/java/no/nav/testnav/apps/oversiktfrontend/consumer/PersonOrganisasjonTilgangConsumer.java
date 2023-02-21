package no.nav.testnav.apps.oversiktfrontend.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetPersonOrganisasjonerTilgangCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.apps.oversiktfrontend.credentials.PersonOrganisasjonTilgangServiceProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final PersonOrganisasjonTilgangServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangConsumer(
            PersonOrganisasjonTilgangServiceProperties serviceProperties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper) {

        this.serviceProperties = serviceProperties;
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
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner() {
        return tokenExchange.exchange(serviceProperties)
                .flatMapMany(accessToken -> new GetPersonOrganisasjonerTilgangCommand(webClient, accessToken.getTokenValue()).call());
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetPersonOrganisasjonTilgangCommand(webClient, accessToken.getTokenValue(), organisasjonsnummer).call())
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException,
                        throwable -> {
                            log.warn("Person har ikke tilgang til organisasjon {}.", organisasjonsnummer);
                            return Mono.empty();
                        })
                .flatMap(value -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }
}
