package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlPerson;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.Request;
import no.nav.testnav.apps.personservice.consumer.header.PdlHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetPdlPersonCommand implements Callable<Mono<PdlPerson>> {
    private static final String TEMA_GENERELL = "GEN";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<PdlPerson> call() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("ident", ident);
        variables.put("historikk", true);

        String query = null;
        InputStream queryStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("pdl/pdlQuery.graphql");
        try {
            query = new BufferedReader(new InputStreamReader(queryStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Lesing av queryressurs feilet", e);
        }

        Request graphQLRequest = Request.builder()
                .query(query)
                .variables(variables)
                .build();


        return webClient
                .post()
                .uri("/pdl-api/graphql")
                .header(AUTHORIZATION, "Bearer " + token)
                .header(PdlHeaders.HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(PdlHeaders.TEMA, TEMA_GENERELL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(graphQLRequest))
                .retrieve()
                .bodyToMono(PdlPerson.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved henting av person fra pdl. Feilmelding: {}.",
                                ((WebClientResponseException) error).getResponseBodyAsString(),
                                error
                        );
                    } else {
                        log.error("Feil ved henting av person fra pdl.", error);
                    }
                });
    }
}
