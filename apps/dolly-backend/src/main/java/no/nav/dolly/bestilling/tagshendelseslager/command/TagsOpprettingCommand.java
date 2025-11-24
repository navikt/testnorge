package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class TagsOpprettingCommand implements Callable<Mono<TagsOpprettingResponse>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String TAGS = "tags";

    private final WebClient webClient;
    private final List<String> identer;
    private final List<Tags> tagVerdier;
    private final String token;

    public Mono<TagsOpprettingResponse> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .queryParam(TAGS, tagVerdier)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(identer))
                .retrieve()
                .toEntity(TagsOpprettingResponse.class)
                .map(status -> TagsOpprettingResponse
                        .builder()
                        .message(Optional.ofNullable(status.getBody()).map(TagsOpprettingResponse::getMessage).orElse(null))
                        .details(Optional.ofNullable(status.getBody()).map(TagsOpprettingResponse::getDetails).orElse(null))
                        .status(HttpStatus.valueOf(status.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log));
    }
}