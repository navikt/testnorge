package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
public final class CheckAliveUtil {

    public static Map<String, String> checkConsumerAlive(NaisServerProperties serviceProperties, WebClient webClient, TokenExchange tokenService) {
        try {
            return Map.of(serviceProperties.getName(), serviceProperties.checkIsAlive(webClient,
                    "Bearer " + tokenService.exchange(serviceProperties).block().getTokenValue()));
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }

    public static Map<String, String> checkConsumerAlive(ServerProperties serviceProperties, WebClient webClient, TokenExchange tokenService) {
        try {
            var token = "Bearer " + tokenService.exchange(serviceProperties).block().getTokenValue();
            return Map.of(serviceProperties.getName(), checkIsAlive(webClient, token, serviceProperties));
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }

    private String checkIsAlive(WebClient webClient, String accessToken, ServerProperties serverProperties) {
        try {
            ResponseEntity<Void> response = webClient.get().uri(uriBuilder -> uriBuilder
                            .pathSegment(serverProperties.getName().contains("proxy") ? "proxy" : null)
                            .pathSegment("internal", "isAlive")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .retrieve().toBodilessEntity()
                    .block();
            if (nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
                return response.getStatusCode().name();
            }
        } catch (WebClientResponseException ex) {
            String feilmelding = String.format("%s, URL: %s", ex.getStatusCode(), serverProperties.getUrl());
            log.error(feilmelding, ex);
            return feilmelding;
        }
        return null;
    }

}
