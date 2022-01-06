package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.importfratpsfservice.utils.ErrorhandlerUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PdlForvalterOrdreCommand implements Callable<Mono<JsonNode>> {

    private static final String PDL_PERSON_URL = "/api/v1/personer/{ident}/ordre";
    private static final String PDL_FORVALTER = "Ordre (POST) til PDL-forvalter: ";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<JsonNode> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(PDL_PERSON_URL).build(ident))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(throwable -> ErrorhandlerUtils.handleError(throwable, PDL_FORVALTER));
    }
}
