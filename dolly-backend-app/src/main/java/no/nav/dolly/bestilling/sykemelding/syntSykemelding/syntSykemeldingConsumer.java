package no.nav.dolly.bestilling.sykemelding.syntSykemelding;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class syntSykemeldingConsumer {

    public static final String SYNT_SYKEMELDING_URL = "/api/v1/synt-sykemelding";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "opprett" })
    public ResponseEntity
}
