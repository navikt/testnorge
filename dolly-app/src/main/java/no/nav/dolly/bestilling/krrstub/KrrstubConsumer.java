package no.nav.dolly.bestilling.krrstub;

import static java.lang.String.format;

import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
public class KrrstubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_PERSONIDENT = "Nav-Personident";
    private static final String DIGITAL_KONTAKT_URL = "/api/v1/kontaktinformasjon";
    private static final String PERSON_DIGITAL_KONTAKT_URL = "/api/v1/person/kontaktinformasjon";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity readDigitalKontaktdata(String ident) {

        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getKrrStub().getUrl() + PERSON_DIGITAL_KONTAKT_URL))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .header(NAV_PERSONIDENT, ident)
                        .build(), DigitalKontaktdata[].class);
    }

    public ResponseEntity<Object> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getKrrStub().getUrl() + DIGITAL_KONTAKT_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .body(digitalKontaktdata), Object.class);
    }

    public ResponseEntity<Object> deleteDigitalKontaktdata(Long id) {

        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format("%s%s/%s", providersProps.getKrrStub().getUrl(), DIGITAL_KONTAKT_URL, id.toString())))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .build(), Object.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}