package no.nav.registre.aareg.security.sts;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static no.nav.registre.aareg.properties.Environment.PREPROD;
import static no.nav.registre.aareg.properties.Environment.TEST;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.properties.CredentialsProps;
import no.nav.registre.aareg.properties.Environment;

@Service
@RequiredArgsConstructor
public class StsOidcService {

    private final RestTemplate restTemplate;
    private final StsOidcFasitConsumer stsOidcFasitConsumer;
    private final CredentialsProps credentialsProps;

    private final Map<Environment, String> idToken = new EnumMap<>(Environment.class);
    private final Map<Environment, LocalDateTime> expiry = new EnumMap<>(Environment.class);

    public String getIdToken(String environment) {
        var env = getEnv(environment);
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
                            throw new TestnorgeAaregFunctionalException("Sikkerhet-token kunne ikke fornyes", e);
                        }
                        throw e;
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
        var getRequest = RequestEntity
                .get(URI.create(stsOidcFasitConsumer.getStsOidcService(env).concat("?grant_type=client_credentials&scope=openid")))
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Basic " +
                        Base64.getEncoder().encodeToString((
                                credentialsProps.getUsername(env) + ":" +
                                        credentialsProps.getPassword(env)).getBytes(UTF_8)))
                .build();
        var responseEntity = restTemplate.exchange(getRequest, JsonNode.class).getBody();

        if (isNull(responseEntity)) {
            return;
        }
        expiry.put(env, LocalDateTime.now().plusSeconds((responseEntity).get("expires_in").asLong()));
        idToken.put(env, "Bearer " + (responseEntity).get("access_token").asText());
    }
}
