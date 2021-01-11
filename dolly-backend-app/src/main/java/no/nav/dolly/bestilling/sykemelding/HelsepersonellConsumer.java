package no.nav.dolly.bestilling.sykemelding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.HelsepersonellListeDTO;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelsepersonellConsumer {

    private static final String HELSEPERSONELL_URL = "/api/v1/helsepersonell";

    private final ProvidersProps providersProps;
    private final TokenService accessTokenService;
    private final WebClient webclient = WebClient.builder().build();

    @Value("${HELSEPERSONELL_CLIENT_ID}")
    private String helsepersonellClientId;

    @Timed(name = "providers", tags = { "operation", "leger-hent" })
    public ResponseEntity<HelsepersonellListeDTO> getHelsepersonell() {

        AccessToken accessToken = accessTokenService.getAccessToken(
                new AccessScopes("api://" + helsepersonellClientId + "/.default")
        );
        log.info("Henter helsepersonell...");
        return webclient
                .get()
                .uri(providersProps.getHelsepersonell().getUrl() + HELSEPERSONELL_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(HelsepersonellListeDTO.class)
                .block();
    }
}
