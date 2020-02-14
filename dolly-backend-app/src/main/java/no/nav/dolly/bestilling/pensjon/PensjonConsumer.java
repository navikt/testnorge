package no.nav.dolly.bestilling.pensjon;

import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeys.KILDE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPerson;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonConsumer {

    private static final String PENSJON_OPPRETT_PERSON_URL = "/api/person";
    private static final String MILJOER_HENT_TILGJENGELIGE_URL = "/api/miljo";
    private static final String PREPROD_ENV = "q";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    public ResponseEntity opprettPerson(OpprettPerson opprettPerson) {

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getPensjon().getUrl() + PENSJON_OPPRETT_PERSON_URL))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                        .header(HEADER_NAV_CALL_ID, getCallId())
                        .header(HEADER_NAV_CONSUMER_ID, KILDE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(opprettPerson),
                JsonNode.class
        );
    }

    public Set<String> getMiljoer() {

        try {
            ResponseEntity responseEntity = restTemplate.exchange(
                    RequestEntity.get(URI.create(providersProps.getPensjon().getUrl() + MILJOER_HENT_TILGJENGELIGE_URL))
                            .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                            .header(HEADER_NAV_CALL_ID, getCallId())
                            .header(HEADER_NAV_CONSUMER_ID, KILDE)
                            .build(),
                    String[].class
            );
            return responseEntity.hasBody() ? Sets.newHashSet((String[]) responseEntity.getBody()) : emptySet();

        } catch (RuntimeException e) {

            log.error("Feilet å lese tilgjengelige miljøer fra pensjon. {}", e.getMessage(), e);
            return emptySet();
        }
    }

    private static String getCallId() {
        return format("%s %s", KILDE, UUID.randomUUID().toString());
    }
}
