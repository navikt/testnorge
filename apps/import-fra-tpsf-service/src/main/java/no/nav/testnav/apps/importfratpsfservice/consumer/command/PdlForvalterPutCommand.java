package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.exception.BadRequestException;
import no.nav.testnav.apps.importfratpsfservice.exception.NotFoundException;
import no.nav.testnav.apps.importfratpsfservice.utils.ErrorhandlerUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlForvalterPutCommand implements Callable<Mono<String>> {

    private static final String PDL_PERSON_URL = "/api/v1/personer/{ident}";
    private static final String PDL_FORVALTER = "Oppretting (PUT) til PDL-forvalter: ";

    private final WebClient webClient;
    private final String ident;
    private final PersonUpdateRequestDTO personUpdateRequest;
    private final String token;

    @Override
    public Mono<String> call() {

        return webClient
                .put()
                .uri(builder -> builder.path(PDL_PERSON_URL).build(ident))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("overwrite", "true")
                .header("relaxed", "true")
                .body(BodyInserters.fromValue(personUpdateRequest))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> ErrorhandlerUtils.handleError(throwable, PDL_FORVALTER));
    }
}
