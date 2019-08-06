package no.nav.dolly.bestilling.krrstub;

import static java.lang.String.format;
import static no.nav.dolly.sts.StsOidcService.getUserIdToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class KrrStubConsumer {

    private static final String KRR_STUB_DIGITAL_KONTAKT = "/api/v1/kontaktinformasjon";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public ResponseEntity<Object> createDigitalKontaktdata(Long bestillingsid, DigitalKontaktdataRequest digitalKontaktdata) {

        return restTemplate.exchange(RequestEntity.post(URI.create(format("%s%s", providersProps.getKrrStub().getUrl(), KRR_STUB_DIGITAL_KONTAKT)))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Nav-Call-Id", Long.toString(bestillingsid))
                .header("Nav-Consumer-Id", getUserIdToken())
                .body(digitalKontaktdata), Object.class);
    }
}