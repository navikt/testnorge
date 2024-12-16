package no.nav.dolly.web.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.Consumers;
import no.nav.dolly.web.consumers.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Altinn3PersonOrganisasjonTilgangConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final AccessService accessService;

    public Altinn3PersonOrganisasjonTilgangConsumer(
            Consumers consumers,
            AccessService accessService,
            WebClient.Builder webClientBuilder) {

        this.accessService = accessService;
        serverProperties = consumers.getTestnavAltinn3TilgangService();

        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange exchange) {

        return accessService.getAccessToken(serverProperties, exchange)
                .flatMapMany(accessToken -> new GetPersonOrganisasjonTilgangCommand(webClient, accessToken).call())
                .filter(organisasjonDTO -> organisasjonDTO.getOrganisasjonsnummer().equals(organisasjonsnummer))
                .onErrorResume(
                        WebClientResponseException.class::isInstance,
                        throwable -> {
                            log.warn("Person har ikke tilgang til organisasjon {}.", organisasjonsnummer);
                            return Mono.empty();
                        })
                .reduce(Boolean.FALSE, (acc, value) -> Boolean.TRUE);
    }
}

