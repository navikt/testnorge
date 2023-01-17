package no.nav.dolly.web.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.dolly.web.credentials.TestnavPersonOrganisasjonTilgangServiceProperties;
import no.nav.dolly.web.service.AccessService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final TestnavPersonOrganisasjonTilgangServiceProperties serviceProperties;

    private final AccessService accessService;

    public PersonOrganisasjonTilgangConsumer(
            TestnavPersonOrganisasjonTilgangServiceProperties serviceProperties,
            AccessService accessService,
            ObjectMapper objectMapper,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.accessService = accessService;
        this.serviceProperties = serviceProperties;
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
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange exchange) {
        return accessService.getAccessToken(serviceProperties, exchange)
                .flatMap(accessToken -> new GetPersonOrganisasjonTilgangCommand(webClient, accessToken, organisasjonsnummer).call())
                .onErrorResume(
                        WebClientResponseException.class::isInstance,
                        throwable -> {
                            log.warn("Person har ikke tilgang til organisasjon {}.", organisasjonsnummer);
                            return Mono.empty();
                        })
                .flatMap(value -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }
}

