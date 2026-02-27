package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AltinnAuthorizedPartiesRequestDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AuthorizedPartyDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetAuthorizedPartiesCommand implements Callable<Mono<AuthorizedPartyDTO[]>> {

    private static final String ALTINN_URL = "/accessmanagement/api/v1/resourceowner/authorizedparties";

    private final WebClient webClient;
    private final AltinnAuthorizedPartiesRequestDTO request;
    private final String token;

    @Override
    public Mono<AuthorizedPartyDTO[]> call() {

        log.info("Spørring på bruker {}", request);
        return webClient
                .post()
                .uri(builder -> builder.path(ALTINN_URL)
                        .queryParam("orgCode", "nav")
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AuthorizedPartyDTO[].class)
                .doOnError(WebClientError.logTo(log));
    }
}