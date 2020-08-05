package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.tpsf.IdentMiljoeRequest;
import no.nav.registre.testnorge.person.consumer.dto.tpsf.PersonMiljoeResponse;

@RequiredArgsConstructor
@Slf4j
public class GetTpsPersonCommand implements Callable<PersonMiljoeResponse> {
    private static final String HENT_PERSON_FRA_TPS_URL = "/v1/dolly/testdata/import";

    private final RestTemplate restTemplate;
    private final String tpsfUrl;
    private final String ident;
    private final String miljoe;

    @Override
    public PersonMiljoeResponse call() {

        IdentMiljoeRequest requestBody = new IdentMiljoeRequest(ident, Collections.singletonList(miljoe));
        log.debug("Henter {} fra TPS miljø {}", ident, miljoe);

        var response = restTemplate.exchange(RequestEntity
                .post(URI.create(tpsfUrl + HENT_PERSON_FRA_TPS_URL))
                .body(requestBody), PersonMiljoeResponse[].class);

        if(!response.getStatusCode().is2xxSuccessful() || !response.hasBody() ){
            throw new RuntimeException("Noe gikk galt ved henting av " + ident + " fra TPS");
        }

        if ( response.getBody().length == 0 ) {
            return null;
        }

        if (response.getBody().length > 1) {
            log.warn("Endepunktet returnerte en liste med mer en enn ett element. Det første elementet blir returnert");
        }

        return response.getBody()[0];
    }
}