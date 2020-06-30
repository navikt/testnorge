package no.nav.dolly.bestilling.sykemelding;

import java.net.URI;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingConsumer {

    public static final String SYNT_SYKEMELDING_URL = "/api/v1/synt-sykemelding";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "opprett" })
    public ResponseEntity<String> postSyntSykemelding(SyntSykemeldingRequest sykemeldingRequest) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getSyntSykemelding().getUrl() + SYNT_SYKEMELDING_URL))
                        .body(sykemeldingRequest),
                String.class);
    }

}
