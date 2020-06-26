package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Request;
import no.nav.registre.testnorge.person.consumer.dto.graphql.PdlPerson;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.reflections.Reflections.log;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class GetPersonCommand implements Callable<PdlPerson> {
    private static final String GRAPHQL_URL = "/graphql";

    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final String token;
    private final String tema = "GEN";

    @Override
    public PdlPerson call() {
        Map<String, Object> variables = new HashMap();
        variables.put("ident", ident);
        variables.put("historikk", true);

        String query = null;
        InputStream queryStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("pdl/pdlQuery.graphql");
        try {
            Reader reader = new InputStreamReader(queryStream, StandardCharsets.UTF_8);
            query = reader.toString();
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
                .header(PdlHeaders.TEMA, tema)
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphQLRequest), PdlPerson.class)
                .getBody();
    }
}
