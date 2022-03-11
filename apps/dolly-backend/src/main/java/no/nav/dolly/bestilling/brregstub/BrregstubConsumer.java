package no.nav.dolly.bestilling.brregstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.config.credentials.BrregstubProxyProperties;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Service
public class BrregstubConsumer {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public BrregstubConsumer(TokenExchange tokenService, BrregstubProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public RolleoversiktTo getRolleoversikt(String ident) {

        try {
            return
                    webClient.get().uri(uriBuilder -> uriBuilder
                                    .path(ROLLEOVERSIKT_URL).build())
                            .header(HEADER_NAV_PERSON_IDENT, ident)
                            .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                            .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                            .retrieve()
                            .bodyToMono(RolleoversiktTo.class)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                    .filter(WebClientFilter::is5xxException))
                            .block();

        } catch (WebClientResponseException e) {
            if (HttpStatus.NOT_FOUND != e.getStatusCode()) {
                log.error("Feilet å lese fra BRREGSTUB", e);
            }

        } catch (RuntimeException e) {
            log.error("Feilet å lese fra BRREGSTUB", e);
        }
        return null;
    }

    public ResponseEntity<RolleoversiktTo> postRolleoversikt(RolleoversiktTo rolleoversiktTo) {

        return
                webClient.post().uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                        .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .bodyValue(rolleoversiktTo)
                        .retrieve()
                        .toEntity(RolleoversiktTo.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException))
                        .block();
    }

    public void deleteRolleoversikt(String ident) {

        try {
            webClient.delete().uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .retrieve()
                    .toEntity(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

        } catch (RuntimeException e) {
            log.error("BRREGSTUB: Feilet å slette rolledata for ident {}", ident, e);
        }
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}