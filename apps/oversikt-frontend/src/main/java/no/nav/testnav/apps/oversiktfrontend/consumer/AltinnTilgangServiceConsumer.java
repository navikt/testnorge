package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.config.Consumers;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetAltinnBrukertilgangTilgangCommand;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AltinnTilgangServiceConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public AltinnTilgangServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient,
            GetAuthenticatedUserId getAuthenticatedUserId
    ) {
        serverProperties = consumers.getTestnavAltinn3TilgangService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.getAuthenticatedUserId = getAuthenticatedUserId;
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner() {

        return getAuthenticatedUserId.call()
                .flatMapMany(userId -> tokenExchange.exchange(serverProperties)
                        .flatMapMany(accessToken ->
                                new GetAltinnBrukertilgangTilgangCommand(webClient, userId, accessToken.getTokenValue()).call()));
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer) {

        return Mono.from(getOrganisasjoner()
                .filter(org -> org.getOrganisasjonsnummer().equals(organisasjonsnummer))
                .onErrorResume(
                        WebClientResponseException.class::isInstance,
                        throwable -> {
                            log.warn("Person har ikke tilgang til organisasjon {}.", organisasjonsnummer);
                            return Mono.empty();
                        })
                .flatMap(value -> Mono.just(true))
                .switchIfEmpty(Mono.just(false)));
    }
}
