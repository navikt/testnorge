package no.nav.registre.aareg.consumer.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import no.nav.registre.aareg.consumer.rs.responses.MiljoerResponse;
import no.nav.registre.aareg.properties.ProvidersProps;

@Component
@RequiredArgsConstructor
public class TpsfConsumer {

    private static final String TPSF_GET_ENVIRONMENTS = "/api/v1/miljoer";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public ResponseEntity<MiljoerResponse> hentMiljoer() {
        RequestEntity getRequest = RequestEntity.get(URI.create(providersProps.getTpsf().getUrl() + TPSF_GET_ENVIRONMENTS)).build();
        return restTemplate.exchange(getRequest, MiljoerResponse.class);
    }
}
