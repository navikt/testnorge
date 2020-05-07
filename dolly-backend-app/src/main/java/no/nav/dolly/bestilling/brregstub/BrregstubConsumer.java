package no.nav.dolly.bestilling.brregstub;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.brregstub.domain.OrganisasjonTo;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrregstubConsumer {

    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String ROLLEOVERSIKT_URL = "/api/v1/rolleoversikt";
    private static final String ROLLE_URL = "/api/v1/hentrolle";
    private static final String KODE_ROLLER_URL = "/api/v1/kode/roller";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public ResponseEntity<RolleoversiktTo> getRolleoversikt(String ident) {

        try {
            return restTemplate.exchange(RequestEntity.get(
                    URI.create(providersProps.getBrregstub().getUrl() + ROLLEOVERSIKT_URL))
                    .header(NAV_PERSON_IDENT, ident)
                    .build(), RolleoversiktTo.class);

        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND != e.getStatusCode()) {
                log.error("Feilet å lese fra BRREGSTUB", e);
            }

        } catch (RuntimeException e) {
            log.error("Feilet å lese fra BRREGSTUB", e);
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Map> getKodeRoller() {

        try {
            return restTemplate.exchange(RequestEntity.get(
                    URI.create(providersProps.getBrregstub().getUrl() + KODE_ROLLER_URL))
                    .build(), Map.class);

        } catch (RuntimeException e) {
            log.error("Feilet å lese koderoller fra BRREGSTUB", e);
        }

        return ResponseEntity.ok(new HashMap());
    }

    public ResponseEntity postRolleoversikt(RolleoversiktTo rolleoversiktTo) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getBrregstub().getUrl() + ROLLEOVERSIKT_URL))
                .body(rolleoversiktTo), RolleoversiktTo.class);
    }

    public ResponseEntity postOrganisasjon(OrganisasjonTo organisasjonTo) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getBrregstub().getUrl() + ROLLE_URL))
                .body(organisasjonTo), OrganisasjonTo.class);
    }

    public void deleteRolleoversikt(String ident) {

        try {
            restTemplate.exchange(RequestEntity.delete(
                    URI.create(providersProps.getBrregstub().getUrl() + ROLLEOVERSIKT_URL))
                    .header(NAV_PERSON_IDENT, ident)
                    .build(), String.class);

        } catch (RuntimeException e) {

            log.error("BRREGSTUB: Feilet å slette rolledata for ident {}", ident, e);
        }
    }
}
