package no.nav.dolly.bestilling.bregstub;

import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.bregstub.domain.RolleutskriftTo;
import no.nav.dolly.properties.ProvidersProps;

@Service
@RequiredArgsConstructor
public class BregstubConsumer {

    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String GRUNNDATA_URL = "/api/v1/rolleutskrift";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public ResponseEntity postGrunndata(RolleutskriftTo rolleutskriftTo) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getBregstub().getUrl() + GRUNNDATA_URL))
                .body(rolleutskriftTo), RolleutskriftTo.class);
    }

    public void deleteGrunndata(String ident) {

        restTemplate.exchange(RequestEntity.delete(
                URI.create(providersProps.getBregstub().getUrl() + GRUNNDATA_URL))
                .header(NAV_PERSON_IDENT, ident)
                .build(), String.class);
    }
}
