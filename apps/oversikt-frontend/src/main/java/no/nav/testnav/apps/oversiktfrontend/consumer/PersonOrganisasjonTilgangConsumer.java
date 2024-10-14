package no.nav.testnav.apps.oversiktfrontend.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.config.Consumers;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetPersonOrganisasjonerTilgangCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavPersonOrganisasjonTilgangService();
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies
                .builder()
                .codecs(
                        config -> {
                            config
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                .build();
        this.webClient = webClientBuilder
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner() {
        return tokenExchange.exchange(serverProperties)
                .flatMapMany(accessToken -> new GetPersonOrganisasjonerTilgangCommand(webClient, accessToken.getTokenValue()).call());
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetPersonOrganisasjonTilgangCommand(webClient, accessToken.getTokenValue(), organisasjonsnummer).call())
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
