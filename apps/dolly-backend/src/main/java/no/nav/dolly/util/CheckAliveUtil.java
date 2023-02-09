package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
public final class CheckAliveUtil {

    private static final String PATTERN = "%s, URL: %s";

    public static Map<String, String> checkConsumerAlive(NaisServerProperties serviceProperties, WebClient webClient, TokenExchange tokenService) {
        try {
            var token = Optional
                    .ofNullable(tokenService.exchange(serviceProperties).block())
                    .orElseThrow(() -> new NullPointerException("Access token is null"));
            return Map.of(
                    serviceProperties.getName(),
                    serviceProperties.checkIsAlive(webClient, "Bearer " + token.getTokenValue())
            );
        } catch (NullPointerException | SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(
                    serviceProperties.getName(),
                    PATTERN.formatted(ex.getMessage(), serviceProperties.getUrl())
            );
        }
    }

    public static Map<String, String> checkConsumerAlive(ServerProperties serviceProperties, WebClient webClient, TokenExchange tokenService) {
        try {
            var token = Optional
                    .ofNullable(tokenService.exchange(serviceProperties).block())
                    .orElseThrow(() -> new NullPointerException("Access token is null"));
            return Map.of(
                    serviceProperties.getName(),
                    checkIsAlive(webClient, "Bearer " + token.getTokenValue(), serviceProperties)
            );
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(
                    serviceProperties.getName(),
                    PATTERN.formatted(ex.getMessage(), serviceProperties.getUrl())
            );
        }
    }

    public static Map<String, Object> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        var map = new HashMap<String, Object>();
        map.put("alive", checkIsAlive(webClient, aliveUrl));
        map.put("ready", checkIsAlive(webClient, readyUrl));
        return map;
    }

    private String checkIsAlive(WebClient webClient, String accessToken, ServerProperties serverProperties) {
        try {
            ResponseEntity<Void> response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(serverProperties.getName().contains("proxy") ? "proxy" : null)
                            .pathSegment("internal", "isAlive")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            if (nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
                return response.getStatusCode().name();
            }
            return HttpStatus.INTERNAL_SERVER_ERROR.name();
        } catch (WebClientResponseException ex) {
            String feilmelding = PATTERN.formatted(ex.getStatusCode(), serverProperties.getUrl());
            log.error(feilmelding, ex);
            return feilmelding;
        }
    }

    private String checkIsAlive(WebClient webClient, String url) {
        try {
            ResponseEntity<Void> response = webClient.get().uri(url)
                    .retrieve().toBodilessEntity()
                    .block();
            if (nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
                return response.getStatusCode().name();
            }
        } catch (WebClientResponseException ex) {
            String feilmelding = PATTERN.formatted(ex.getStatusCode(), url);
            log.error(feilmelding, ex);
            return feilmelding;
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

}
