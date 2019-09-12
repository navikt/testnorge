package no.nav.dolly.bestilling.udistub;

import static java.lang.String.format;

import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
public class UdiStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String UDI_STUB_PERSON = "/api/v1/person";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity<UdiPersonControllerResponse> createUdiPerson(UdiPerson udiPerson) {
        try {
            return restTemplate.exchange(RequestEntity.post(URI.create(providersProps.getUdiStub().getUrl() + UDI_STUB_PERSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(NAV_CALL_ID, getNavCallId())
                            .header(NAV_CONSUMER_ID, CONSUMER)
                            .body(udiPerson),
                    UdiPersonControllerResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return new ResponseEntity<>(UdiPersonControllerResponse.builder()
                    .reason(e.getResponseBodyAsString())
                    .status(e.getStatusCode())
                    .build(), e.getStatusCode());
        }
    }

    public void deleteUdiPerson(String ident) {
        try {
            restTemplate.exchange(RequestEntity.delete(URI.create(providersProps.getUdiStub().getUrl() + UDI_STUB_PERSON))
                            .header(NAV_CALL_ID, getNavCallId())
                            .header(NAV_CONSUMER_ID, CONSUMER)
                            .header(NAV_PERSON_IDENT, ident)
                            .build(),
                    UdiPersonControllerResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UdiStubException(e);
            }
        }
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}