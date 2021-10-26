package no.nav.dolly.bestilling.brregstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.config.credentials.BrregstubProxyProperties;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class BrregstubConsumer {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public BrregstubConsumer(TokenService tokenService, BrregstubProxyProperties serverProperties, ObjectMapper objectMapper) {
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
                            .retrieve().toEntity(RolleoversiktTo.class)
                            .block()
                            .getBody();

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
                        .bodyValue(rolleoversiktTo)
                        .retrieve().toEntity(RolleoversiktTo.class)
                        .block();
    }

    public void deleteRolleoversikt(String ident) {

        try {
            webClient.delete().uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .retrieve().toEntity(String.class)
                    .block();

        } catch (RuntimeException e) {
            log.error("BRREGSTUB: Feilet å slette rolledata for ident {}", ident, e);
        }
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}