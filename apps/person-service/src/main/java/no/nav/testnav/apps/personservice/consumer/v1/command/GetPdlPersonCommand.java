package no.nav.testnav.apps.personservice.consumer.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.v1.header.PdlHeaders;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.PdlPerson;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.Request;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
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
    private final String url;
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
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .path("/graphql")
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(PdlHeaders.HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(PdlHeaders.TEMA, TEMA_GENERELL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(graphQLRequest))
                .retrieve()
                .bodyToMono(PdlPerson.class)
                .retryWhen(WebClientError.is5xxException());

    }

}
