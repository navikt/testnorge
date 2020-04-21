package no.nav.registre.inntekt.security.sts;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import no.nav.registre.inntekt.security.properties.CredentialProps;
import no.nav.registre.inntekt.security.properties.Environment;
import no.nav.registre.inntekt.utils.FunctionalException;

@Service
@RequiredArgsConstructor
public class StsOidcService {

    private final RestTemplate restTemplate;
    private final CredentialProps credentialProps;

    private final Map<Environment, String> idToken = new EnumMap<>(Environment.class);
    private final Map<Environment, LocalDateTime> expiry = new EnumMap<>(Environment.class);

    public String getIdToken(String environment) {
        var env = Environment.getEnv(environment);
        updateTokenIfNeeded(env);

        return idToken.get(env);
    }

    private void updateTokenIfNeeded(Environment env) {
        synchronized (this) {
            if (shouldRefresh(env)) {
                try {
                    updateToken(env);
                } catch (RuntimeException e) {
                    throw new FunctionalException("Sikkerhets-token kunne ikke fornyes.", e);
                }
            }
        }
    }

    private boolean shouldRefresh(Environment env) {
        return !expiry.containsKey(env) || LocalDateTime.now().plusMinutes(1L).isAfter(expiry.get(env));
    }

    private void updateToken(Environment env) {
        var getRequest = RequestEntity
                .get(URI.create(credentialProps.getTokenUrl(env).concat("?grant_type=client_credentials&scope=openid")))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Basic " +
                        Base64.getEncoder().encodeToString(
                                (credentialProps.getUsername(env) + ":" + credentialProps.getPassword(env)).getBytes(UTF_8)))
                .build();

        var responseEntity = restTemplate.exchange(getRequest, JsonNode.class).getBody();
        if (Objects.isNull(responseEntity)) {
            return;
        }

        expiry.put(env, LocalDateTime.now().plusSeconds(responseEntity.get("expires_in").asLong()));
        idToken.put(env, "Bearer " + responseEntity.get("access_token").asText());
    }
}
