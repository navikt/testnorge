package no.nav.dolly.bestilling.sykemelding;

import java.net.URI;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.LegeListeDTO;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelsepersonellConsumer {

    private static final String LEGER_URL = "/api/v1/helsepersonell/leger";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "leger-hent" })
    public ResponseEntity<LegeListeDTO> getLeger() {

        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getHelsepersonell().getUrl() + LEGER_URL))
                        .build(),
                LegeListeDTO.class);
    }
}
