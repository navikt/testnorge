package no.nav.dolly.web.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.Consumers;
import no.nav.dolly.web.consumers.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    private final AccessService accessService;

    public PersonOrganisasjonTilgangConsumer(
            Consumers consumers,
            AccessService accessService,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {

        this.accessService = accessService;
        serverProperties = consumers.getTestnavPersonOrganisasjonTilgangService();
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        this.webClient = webClientBuilder
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange exchange) {
        return accessService.getAccessToken(serverProperties, exchange)
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

