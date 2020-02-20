package no.nav.registre.inst.security;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class StsOidcService {

    private static final String MILJOE_Q = "q";
    private static final String MILJOE_T = "t";

    private final RestTemplate restTemplate;

    @Value("${serviceuser_username_q}")
    private String usernameQ;

    @Value("${serviceuser_password_q}")
    private String passwordQ;

    @Value("${sts.url.q}")
    private String stsUrlQ;

    @Value("${serviceuser_username_t}")
    private String usernameT;

    @Value("${serviceuser_password_t}")
    private String passwordT;

    @Value("${sts.url.t}")
    private String stsUrlT;

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
            url = stsUrlQ;
        } else {
            url = stsUrlT;
        }
        RequestEntity<?> entity = RequestEntity
                .get(URI.create(url.concat("?grant_type=client_credentials&scope=openid")))
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, generateBasicAuthToken(miljoe))
                .build();

        JsonNode result = restTemplate.exchange(entity, JsonNode.class).getBody();
        if (result == null) {
            return;
        }
        if (MILJOE_Q.equals(miljoe)) {
            expiryQ = LocalDateTime.now().plusSeconds(result.get("expires_in").asLong());
            idTokenQ = "Bearer " + result.get("access_token").asText();
        } else {
            expiryT = LocalDateTime.now().plusSeconds(result.get("expires_in").asLong());
            idTokenT = "Bearer " + result.get("access_token").asText();
        }
    }

    private String generateBasicAuthToken(String miljoe) {
        if (MILJOE_Q.equals(miljoe)) {
            return "Basic " + Base64.getEncoder().encodeToString((usernameQ + ":" + passwordQ).getBytes(UTF_8));
        } else {
            return "Basic " + Base64.getEncoder().encodeToString((usernameT + ":" + passwordT).getBytes(UTF_8));
        }
    }
}
