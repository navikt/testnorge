package no.nav.dolly.security.config;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Getter
@Setter
@Slf4j
public class NaisServerProperties extends ServerProperties {

    public String getAccessToken(TokenExchange tokenExchange) {
        if (isNull(MDC.getCopyOfContextMap())) {
            MDC.setContextMap(new HashMap<>());
        }
        var token = tokenExchange.exchange(this).block();
        if (isNull(token)) {
            throw new SecurityException(String.format("Klarte ikke å generere AccessToken for %s", getName()));
        }
        return "Bearer " + token.getTokenValue();
    }

    public String checkIsAlive(WebClient webClient, String accessToken) {
        try {
            ResponseEntity<Void> response = webClient.get().uri(uriBuilder -> uriBuilder
                            .pathSegment(getName().contains("proxy") ? "proxy" : null)
                            .pathSegment("internal", "isAlive")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .retrieve().toBodilessEntity()
                    .block();
            if (nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
                return response.getStatusCode().name();
            }
        } catch (WebClientResponseException ex) {
            String feilmelding = String.format("%s, URL: %s", ex.getStatusCode(), getUrl());
            log.error(feilmelding, ex);
            return feilmelding;
        }
        return null;
    }
}
