package no.nav.dolly.bestilling.krrstub;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;

import java.net.URI;
import java.util.UUID;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
    private static final String DIGITAL_KONTAKT_URL = "/api/v1/kontaktinformasjon";
    private static final String PERSON_DIGITAL_KONTAKT_URL = "/api/v1/person/kontaktinformasjon";

    private RestTemplate restTemplate;
    private ProvidersProps providersProps;

    public KrrstubConsumer(RestTemplateBuilder restTemplateBuilder, ProvidersProps providersProps) {
        restTemplate = restTemplateBuilder.build();
        this.providersProps =providersProps;
    }

    public ResponseEntity readDigitalKontaktdata(String ident) {

        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getKrrStub().getUrl() + PERSON_DIGITAL_KONTAKT_URL))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_PERSON_IDENT, ident)
                        .build(), DigitalKontaktdata[].class);
    }

    public ResponseEntity<Object> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getKrrStub().getUrl() + DIGITAL_KONTAKT_URL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(digitalKontaktdata), Object.class);
    }

    public ResponseEntity<Object> deleteDigitalKontaktdata(Long id) {

        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format("%s%s/%s", providersProps.getKrrStub().getUrl(), DIGITAL_KONTAKT_URL, id.toString())))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(), Object.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}