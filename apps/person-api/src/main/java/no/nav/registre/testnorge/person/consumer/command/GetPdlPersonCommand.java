package no.nav.registre.testnorge.person.consumer.command;

import static org.reflections.Reflections.log;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.PdlPerson;
import no.nav.registre.testnorge.person.consumer.dto.pdl.graphql.Request;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;

@RequiredArgsConstructor
public class GetPdlPersonCommand implements Callable<PdlPerson> {
    private static final String GRAPHQL_URL = "/graphql";
    private static final String TEMA_GENERELL = "GEN";

    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final String token;

    @Override
    public PdlPerson call() {
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
            log.error("Lesing av queryressurs feilet");
        }

        Request graphQLRequest = Request.builder()
                .query(query)
                .variables(variables)
                .build();

        return restTemplate.exchange(RequestEntity.post(
                URI.create(url + GRAPHQL_URL))
                .header(AUTHORIZATION, "Bearer " + token)
                .header(PdlHeaders.HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(PdlHeaders.HEADER_NAV_CONSUMER_TOKEN, "Bearer " + token)
                .header(PdlHeaders.TEMA, TEMA_GENERELL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphQLRequest), PdlPerson.class)
                .getBody();
    }
}
