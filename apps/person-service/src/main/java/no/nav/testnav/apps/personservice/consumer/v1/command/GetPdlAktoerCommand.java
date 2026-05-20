package no.nav.testnav.apps.personservice.consumer.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.v1.header.PdlHeaders;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.PdlAktoer;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.Request;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetPdlAktoerCommand implements Callable<Mono<PdlAktoer>> {

    private static final String FILENAME = "pdl/pdlPersonQuery.graphql";
    private static final String TEMA_GENERELL = "GEN";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;

    @Override
    public Mono<PdlAktoer> call() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("ident1", ident);

        String query = null;
        var stream = Optional
                .ofNullable(Thread
                        .currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(FILENAME))
                .orElseThrow(() -> new IllegalStateException("Finner ikke fil %s".formatted(FILENAME)));
        try {
            query = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Lesing av queryressurs feilet", e);
        }

        Request graphQLRequest = Request.builder()
                .query(query)
                .variables(variables)
                .build();

        log.debug("GetPdlAktoerCommand: Sender request til {} for ident {}", url, ident);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .path("/graphql")
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(PdlHeaders.HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(PdlHeaders.TEMA, TEMA_GENERELL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(graphQLRequest))
                .retrieve()
                .bodyToMono(PdlAktoer.class)
                .doOnSuccess(response -> log.debug("GetPdlAktoerCommand: Mottok respons fra {} for ident {}", url, ident))
                .doOnError(e -> log.error("GetPdlAktoerCommand: Feil ved kall til {} for ident {}: {} - {}", 
                        url, ident, e.getClass().getSimpleName(), e.getMessage(), e))
                .retryWhen(WebClientError.is5xxException());

    }

}
