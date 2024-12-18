package no.nav.dolly.web.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.Consumers;
import no.nav.dolly.web.consumers.command.PostPersonOrganisasjonTilgangCommand;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
public class Altinn3PersonOrganisasjonTilgangConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final AccessService accessService;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public Altinn3PersonOrganisasjonTilgangConsumer(
            Consumers consumers,
            AccessService accessService,
            WebClient.Builder webClientBuilder,
            GetAuthenticatedUserId getAuthenticatedUserId) {

        this.accessService = accessService;
        serverProperties = consumers.getTestnavAltinn3TilgangService();

        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
        this.getAuthenticatedUserId = getAuthenticatedUserId;
    }

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange exchange) {

        return getAuthenticatedUserId
                .call()
                .flatMap(userId -> accessService.getAccessToken(serverProperties, exchange)
                        .flatMapMany(accessToken -> new PostPersonOrganisasjonTilgangCommand(webClient, userId, accessToken).call())
                        .filter(organisasjonDTO -> organisasjonDTO.getOrganisasjonsnummer().equals(organisasjonsnummer))
                        .onErrorResume(
                                WebClientResponseException.class::isInstance,
                                throwable -> {
                                    log.warn("Person har ikke tilgang til organisasjon {}.", organisasjonsnummer);
                                    return Mono.empty();
                                })
                        .reduce(Boolean.FALSE, (acc, value) -> Boolean.TRUE));
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner(ServerWebExchange exchange) {

        return getAuthenticatedUserId
                .call()
                .doOnNext(pid -> log.info("getOrganisasjoner userid {}", pid))
                .flatMap(Altinn3PersonOrganisasjonTilgangConsumer::getUserId)
                .flatMapMany(userId ->
                        accessService.getAccessToken(serverProperties, exchange)
                                .flatMapMany(accessToken -> new PostPersonOrganisasjonTilgangCommand(webClient, userId, accessToken).call()));
    }

    public static Mono<String> getUserId(String token) {

        if (token.length() == 11) {
            return Mono.just(token);
        } else {
            var info = Arrays.toString(Base64.getDecoder().decode(token.split("\\.")[1]));
            var start = info.indexOf("\"pid\":") + 7;
            return Mono.just(info.substring(start, start + 11));
        }
    }
}

