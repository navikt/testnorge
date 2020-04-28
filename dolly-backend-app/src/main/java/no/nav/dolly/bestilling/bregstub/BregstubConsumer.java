package no.nav.dolly.bestilling.bregstub;

import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.bregstub.domain.RolleutskriftTo;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class BregstubConsumer {

    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String GRUNNDATA_URL = "/api/v1/rolleoversikt";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public ResponseEntity postGrunndata(RolleutskriftTo rolleutskriftTo) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getBregstub().getUrl() + GRUNNDATA_URL))
                .body(rolleutskriftTo), RolleutskriftTo.class);
    }

    public void deleteGrunndata(String ident) {

        try {
            restTemplate.exchange(RequestEntity.delete(
                    URI.create(providersProps.getBregstub().getUrl() + GRUNNDATA_URL))
                    .header(NAV_PERSON_IDENT, ident)
                    .build(), String.class);

        } catch (RuntimeException e){

            log.error("BREGSTUB: Feilet å slette rolledata for ident {}", ident, e);
        }
    }
}
