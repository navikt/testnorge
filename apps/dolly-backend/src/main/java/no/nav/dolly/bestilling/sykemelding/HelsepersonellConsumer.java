package no.nav.dolly.bestilling.sykemelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.HelsepersonellListeDTO;
import no.nav.dolly.config.credentials.HelsepersonellServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Service
public class HelsepersonellConsumer {

    private static final String HELSEPERSONELL_URL = "/api/v1/helsepersonell";

    private final TokenExchange accessTokenService;
    private final HelsepersonellServiceProperties serviceProperties;
    private final WebClient webClient;

    public HelsepersonellConsumer(
            TokenExchange accessTokenService,
            HelsepersonellServiceProperties serviceProperties,
            WebClient.Builder webClientBuilder
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = webClientBuilder
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "leger-hent"})
    public HelsepersonellListeDTO getHelsepersonell() {

        log.info("Henter helsepersonell...");

        return accessTokenService.exchange(serviceProperties)
                .flatMap(token -> webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder.path(HELSEPERSONELL_URL).build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(HelsepersonellListeDTO.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)))
                .block();
    }
}
