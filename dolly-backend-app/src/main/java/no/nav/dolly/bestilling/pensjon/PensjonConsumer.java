package no.nav.dolly.bestilling.pensjon;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_FORBRUKER_ID;
import static no.nav.dolly.domain.CommonKeys.KILDE;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPerson;
import no.nav.dolly.properties.ProvidersProps;

@Service
@RequiredArgsConstructor
public class PensjonConsumer {

    private static final String SYNT_MILJOE = "q2";
    private static final String PENSJON_OPPRETT_PERSON_URL = "/person";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public ResponseEntity opprettPerson(OpprettPerson opprettPerson) {

        opprettPerson.setMiljo(asList(SYNT_MILJOE));
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlForvalter().getUrl() + PENSJON_OPPRETT_PERSON_URL))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_FORBRUKER_ID, KILDE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(opprettPerson), JsonNode.class);
    }

    private static String getCallId() {
        return format("%s %s", KILDE, UUID.randomUUID().toString());
    }
}
