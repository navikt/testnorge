package no.nav.dolly.bestilling.brregstub;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrregstubConsumer {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public RolleoversiktTo getRolleoversikt(String ident) {

        try {
            return restTemplate.exchange(RequestEntity.get(
                    URI.create(providersProps.getBrregstub().getUrl() + ROLLEOVERSIKT_URL))
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .build(), RolleoversiktTo.class).getBody();

        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND != e.getStatusCode()) {
                log.error("Feilet å lese fra BRREGSTUB", e);
            }

        } catch (RuntimeException e) {
            log.error("Feilet å lese fra BRREGSTUB", e);
        }

        return null;
    }

    public ResponseEntity postRolleoversikt(RolleoversiktTo rolleoversiktTo) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getBrregstub().getUrl() + ROLLEOVERSIKT_URL))
                .body(rolleoversiktTo), RolleoversiktTo.class);
    }

    public void deleteRolleoversikt(String ident) {

        try {
            restTemplate.exchange(RequestEntity.delete(
                    URI.create(providersProps.getBrregstub().getUrl() + ROLLEOVERSIKT_URL))
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .build(), String.class);

        } catch (RuntimeException e) {

            log.error("BRREGSTUB: Feilet å slette rolledata for ident {}", ident, e);
        }
    }
}
