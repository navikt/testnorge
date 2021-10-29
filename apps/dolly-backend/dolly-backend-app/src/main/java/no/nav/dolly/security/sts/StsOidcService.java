package no.nav.dolly.security.sts;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.properties.CredentialsProps;
import no.nav.dolly.properties.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static no.nav.dolly.properties.Environment.PREPROD;
import static no.nav.dolly.properties.Environment.TEST;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class StsOidcService {

    private final RestTemplate restTemplate;
    private final CredentialsProps credentialsProps;

    @Value("${sts.token.provider.test}")
    private String stsTokenProviderTestUrl;

    @Value("${sts.token.provider.preprod}")
    private String stsTokenProviderPreprodUrl;

    private Map<Environment, String> idToken = new EnumMap<>(Environment.class);
    private Map<Environment, LocalDateTime> expiry = new EnumMap<>(Environment.class);

    public String getIdToken(String environment) {

        Environment env = getEnv(environment);
        updateTokenIfNeeded(env);

        return idToken.get(env);
    }

    private Environment getEnv(String environment) {
        return environment.contains("q") ? PREPROD : TEST;
    }

    private void updateTokenIfNeeded(Environment env) {
        if (shouldRefresh(env)) {
            synchronized (this) {
                if (shouldRefresh(env)) {
                    try {
                        updateToken(env);
                    } catch (RuntimeException e) {
                        if (hasExpired(env)) {
                            throw new DollyFunctionalException("Sikkerhet-token kunne ikke fornyes", e);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldRefresh(Environment env) {
        return !expiry.containsKey(env) || LocalDateTime.now().plusMinutes(1).isAfter(expiry.get(env));
    }

    private boolean hasExpired(Environment env) {
        return !expiry.containsKey(env) || LocalDateTime.now().isAfter(expiry.get(env));
    }

    private void updateToken(Environment env) {

        ResponseEntity responseEntity = restTemplate.exchange(RequestEntity
                .get(URI.create((PREPROD == env ? stsTokenProviderPreprodUrl : stsTokenProviderTestUrl)
                        .concat("?grant_type=client_credentials&scope=openid")))
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Basic " +
                        Base64.getEncoder().encodeToString((
                                credentialsProps.getUsername(env) + ":" +
                                        credentialsProps.getPassword(env)).getBytes(UTF_8)))
                .build(), JsonNode.class);

        if (isNull(responseEntity.getBody())) {
            return;
        }
        expiry.put(env, LocalDateTime.now().plusSeconds(((JsonNode) responseEntity.getBody()).get("expires_in").asLong()));
        idToken.put(env, "Bearer " + ((JsonNode) responseEntity.getBody()).get("access_token").asText());
    }
}
