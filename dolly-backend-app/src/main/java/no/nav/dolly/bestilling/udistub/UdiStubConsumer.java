package no.nav.dolly.bestilling.udistub;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags = { "operation", "udi_createPerson" })
    public UdiPersonResponse getUdiPerson(String ident) {

        try {
            ResponseEntity<UdiPersonResponse> response = restTemplate.exchange(RequestEntity.get(
                    URI.create(format("%s%s/%s/", providersProps.getUdiStub().getUrl(), UDISTUB_PERSON, ident)))
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .build(),
                    UdiPersonResponse.class);
            return response.hasBody() ? response.getBody() : null;

        } catch (RuntimeException e) {
            return null;
        }
    }

    @Timed(name = "providers", tags = { "operation", "udi_createPerson" })
    public ResponseEntity<UdiPersonResponse> createUdiPerson(UdiPerson udiPerson) {

        return restTemplate.exchange(RequestEntity.post(URI.create(providersProps.getUdiStub().getUrl() + UDISTUB_PERSON))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(udiPerson),
                UdiPersonResponse.class);
    }

    @Timed(name = "providers", tags = { "operation", "udi_updatePerson" })
    public ResponseEntity<UdiPersonResponse> updateUdiPerson(UdiPerson udiPerson) {

        return restTemplate.exchange(RequestEntity.put(URI.create(providersProps.getUdiStub().getUrl() + UDISTUB_PERSON))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(udiPerson),
                UdiPersonResponse.class);
    }

    @Timed(name = "providers", tags = { "operation", "udi_deletePerson" })
    public void deleteUdiPerson(String ident) {

        try {
            restTemplate.exchange(RequestEntity.delete(URI.create(providersProps.getUdiStub().getUrl() + UDISTUB_PERSON))
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(NAV_PERSON_IDENT, ident)
                    .build(), Object.class);

        } catch (RuntimeException e) {
            errorStatusDecoder.decodeRuntimeException(e);
        }
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}