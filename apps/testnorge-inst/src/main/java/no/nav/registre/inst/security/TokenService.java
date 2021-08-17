package no.nav.registre.inst.security;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class TokenService {

    private static final String MILJOE_Q = "q";
    private static final String MILJOE_T = "t";

    private final RestTemplate restTemplate;

    @Value("${ida.username}")
    private String username;

    @Value("${ida.password}")
    private String password;

    @Value("${testnorge-token-provider-q2.url}")
    private String tokenProviderUrlQ;

    @Value("${testnorge-token-provider-t6.url}")
    private String tokenProviderUrlT;

    private String idTokenQ;
    private String idTokenT;
    private LocalDateTime expiryQ;
    private LocalDateTime expiryT;

    public String getIdTokenQ() {
        updateTokenIfNeeded(MILJOE_Q);

        return idTokenQ;
    }

    public String getIdTokenT() {
        updateTokenIfNeeded(MILJOE_T);

        return idTokenT;
    }

    private void updateTokenIfNeeded(String miljoe) {
        if (shouldRefresh(miljoe)) {
            synchronized (this) {
                if (shouldRefresh(miljoe)) {
                    try {
                        updateToken(miljoe);
                    } catch (RuntimeException e) {
                        if (hasExpired(miljoe)) {
                            throw new TokenServiceException(e);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldRefresh(String miljoe) {
        if (MILJOE_Q.equals(miljoe)) {
            return expiryQ == null || LocalDateTime.now().plusMinutes(1).isAfter(expiryQ);
        } else {
            return expiryT == null || LocalDateTime.now().plusMinutes(1).isAfter(expiryT);
        }
    }

    private boolean hasExpired(String miljoe) {
        if (MILJOE_Q.equals(miljoe)) {
            return expiryQ == null || LocalDateTime.now().isAfter(expiryQ);
        } else {
            return expiryT == null || LocalDateTime.now().isAfter(expiryT);
        }
    }

    private void updateToken(String miljoe) {
        String url;
        if (MILJOE_Q.equals(miljoe)) {
            url = tokenProviderUrlQ;
        } else {
            url = tokenProviderUrlT;
        }
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
        if (MILJOE_Q.equals(miljoe)) {
            expiryQ = LocalDateTime.now().plusSeconds(result.get("expiresIn").asLong());
            idTokenQ = "Bearer " + result.get("idToken").asText();
        } else {
            expiryT = LocalDateTime.now().plusSeconds(result.get("expiresIn").asLong());
            idTokenT = "Bearer " + result.get("idToken").asText();
        }
    }
}
