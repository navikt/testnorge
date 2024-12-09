package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.AltinnConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetAccessListMembersCommand implements Callable<Mono<AltinnResponseDTO>> {

    private static final String ALTINN_URL = "/resourceregistry/api/v1/access-lists/{owner}/{identifier}/members";

    private final WebClient webClient;
    private final String token;
    private final AltinnConfig altinnConfig;

    @Override
    public Mono<AltinnResponseDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(ALTINN_URL)
                        .build(altinnConfig.getOwner(), altinnConfig.getIdentifier()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AltinnResponseDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .doOnSuccess(value -> log.info("Altinn-tilgang hentet"));
    }
}