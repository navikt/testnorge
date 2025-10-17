package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.AltinnConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnAccessListResponseDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonDeleteDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.OrganisasjonCreateDTO.ORGANISASJON_ID;

@Slf4j
@RequiredArgsConstructor
public class DeleteAccessListMemberCommand implements Callable<Mono<AltinnAccessListResponseDTO>> {

    private static final String ALTINN_URL = "/resourceregistry/api/v1/access-lists/{owner}/{identifier}/members";

    private final WebClient webClient;
    private final String token;
    private final OrganisasjonDeleteDTO identifiers;
    private final AltinnConfig altinnConfig;


    @Override
    public Mono<AltinnAccessListResponseDTO> call() {

        return webClient
                .method(HttpMethod.DELETE)
                .uri(builder -> builder.path(ALTINN_URL)
                        .build(altinnConfig.getOwner(), altinnConfig.getIdentifier())
                )
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(identifiers)
                .retrieve()
                .bodyToMono(AltinnAccessListResponseDTO.class)
                .doOnSuccess(value -> log.info("Altinn organisasjontilgang slettet for {}",
                        identifiers.getData().stream()
                                .filter(data -> data.contains(ORGANISASJON_ID))
                                .map(data -> data.split(":"))
                                .map(data -> data[data.length - 1])
                                .collect(Collectors.joining())))
                .doOnError(WebClientError.logTo(log));
    }
}
