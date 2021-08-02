package no.nav.registre.testnorge.arena.security;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Component
public class TokenService {

    private final WebClient webClient;

    @Value("${ida.username}")
    private String username;

    @Value("${ida.password}")
    private String password;

    private String idToken;
    private LocalDateTime expiry;

    public TokenService(
            @Value("${testnorge-token-provider.url}") String tokenProviderUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(tokenProviderUrl).build();
    }

    public String getIdToken() {
        updateTokenIfNeeded();
        return idToken;
    }

    private void updateTokenIfNeeded() {
        if (shouldRefresh()) {
            synchronized (this) {
                if (shouldRefresh()) {
                    try {
                        updateToken();
                    } catch (RuntimeException e) {
                        if (hasExpired()) {
                            throw new TokenServiceException(e);
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
        var result = webClient
                .get()
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("username", username)
                .header("password", password)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (result == null) {
            return;
        }
        expiry = LocalDateTime.now().plusSeconds(result.get("expiresIn").asLong());
        idToken = "Bearer " + result.get("idToken").asText();
    }
}
