package no.nav.registre.testnorge.libs.service;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Slf4j
public class StsOidcTokenService {
    private final WebClient webClient;

    private String token;
    private LocalDateTime expiry;

    public StsOidcTokenService(
            String url,
            String username,
            String password
    ) {
        webClient = WebClient
                .builder()
                .baseUrl(url)
                .defaultHeaders(header -> header.setBasicAuth(username, password))
                .build();
    }

    public String getToken() {
        updateTokenIfNeeded();
        return token;
    }

    private void updateTokenIfNeeded() {
        if (shouldRefresh()) {
            synchronized (this) {
                if (shouldRefresh()) {
                    try {
                        updateToken();
                    } catch (RuntimeException e) {
                        if (hasExpired()) {
                            throw new RuntimeException("Sikkerhet-token kunne ikke fornyes", e);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldRefresh() {
        return expiry == null || LocalDateTime.now().plusMinutes(1).isAfter(expiry);
    }

    private boolean hasExpired() {
        return expiry == null || LocalDateTime.now().isAfter(expiry);
    }

    private void updateToken() {
        var node = webClient
                .get()
                .uri(builder -> builder
                        .queryParam("grant_type", "client_credentials")
                        .queryParam("scope", "openid")
                        .build()
                )
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        expiry = LocalDateTime.now().plusSeconds(node.get("expires_in").asLong());
        token = node.get("access_token").asText();
    }

}
