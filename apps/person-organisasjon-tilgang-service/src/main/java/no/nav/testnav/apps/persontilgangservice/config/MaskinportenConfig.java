package no.nav.testnav.apps.persontilgangservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaskinportenConfig {

    private final String clientId;
    private final String jwkPrivate;
    private final String scope;
    private final String wellKnownUrl;

    public MaskinportenConfig(
            @Value("${MASKINPORTEN_CLIENT_ID}") String clientId,
            @Value("${MASKINPORTEN_CLIENT_JWK}") String jwkPrivate,
            @Value("${MASKINPORTEN_SCOPES}") String scope,
            @Value("${MASKINPORTEN_WELL_KNOWN_URL}") String wellKnownUrl
    ) {
        this.clientId = clientId;
        this.scope = scope;
        this.jwkPrivate = jwkPrivate;
        this.wellKnownUrl = wellKnownUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getJwkPrivate() {
        return jwkPrivate;
    }

    public String getScope() {
        return scope;
    }

    public String getWellKnownUrl() {
        return wellKnownUrl;
    }
}
