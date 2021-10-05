package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetPersonOrganisasjonerTilgangCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.apps.oversiktfrontend.credentials.PersonOrganisasjonTilgangServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final PersonOrganisasjonTilgangServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangConsumer(PersonOrganisasjonTilgangServiceProperties serviceProperties, TokenExchange tokenExchange) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner(ServerWebExchange serverWebExchange) {
        return tokenExchange.generateToken(serviceProperties, serverWebExchange)
                .flatMapMany(accessToken -> new GetPersonOrganisasjonerTilgangCommand(webClient, accessToken.getTokenValue()).call());
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange serverWebExchange) {
        return tokenExchange.generateToken(serviceProperties, serverWebExchange)
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
