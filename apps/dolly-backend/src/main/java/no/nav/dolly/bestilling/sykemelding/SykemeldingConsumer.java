package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.config.credentials.SykemeldingApiProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Service
public class SykemeldingConsumer {

    public static final String SYNT_SYKEMELDING_URL = "/syntetisk/api/v1/synt-sykemelding";
    public static final String DETALJERT_SYKEMELDING_URL = "/sykemelding/api/v1/sykemeldinger";

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public SykemeldingConsumer(
            TokenExchange accessTokenService,
            SykemeldingApiProxyProperties serverProperties,
            ObjectMapper objectMapper
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "syntsykemelding_opprett" })
    public ResponseEntity<String> postSyntSykemelding(SyntSykemeldingRequest sykemeldingRequest) {

        String callId = getNavCallId();
        log.info("Synt Sykemelding sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SYNT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(sykemeldingRequest)
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public ResponseEntity<String> postDetaljertSykemelding(DetaljertSykemeldingRequest detaljertSykemeldingRequest) {

        String callId = getNavCallId();
        log.info("Detaljert Sykemelding sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(DETALJERT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(detaljertSykemeldingRequest)
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
