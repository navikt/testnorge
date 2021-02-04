package no.nav.registre.testnorge.arena.security;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenService {

    private final RestTemplate restTemplate;

    @Value("${ida.username}")
    private String username;

    @Value("${ida.password}")
    private String password;

    @Value("${testnorge-token-provider.url}")
    private String tokenProviderUrl;

    private String idToken;
    private LocalDateTime expiry;

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
        String url = tokenProviderUrl;
        RequestEntity<?> getRequest = RequestEntity
                .get(URI.create(url))
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("username", username)
                .header("password", password)
                .build();

        JsonNode result = restTemplate.exchange(getRequest, JsonNode.class).getBody();
        if (result == null) {
            return;
        }
        expiry = LocalDateTime.now().plusSeconds(result.get("expiresIn").asLong());
        idToken = "Bearer " + result.get("idToken").asText();
    }
}
