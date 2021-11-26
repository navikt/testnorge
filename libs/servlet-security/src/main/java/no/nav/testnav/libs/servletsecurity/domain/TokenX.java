package no.nav.testnav.libs.servletsecurity.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenX {

    private final String clientId;
    private final String jwk;
    private final String wellKnownUrl;

    public TokenX(
            @Value("${TOKEN_X_CLIENT_ID:#{null}}") String clientId,
            @Value("${TOKEN_X_PRIVATE_JWK:#{null}}") String jwk,
            @Value("${TOKEN_X_WELL_KNOWN_URL:#{null}}") String wellKnownUrl
    ) {
        this.clientId = clientId;
        this.jwk = jwk;
        this.wellKnownUrl = wellKnownUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getJwk() {
        return jwk;
    }

    public String getWellKnownUrl() {
        return wellKnownUrl;
    }

    @Override
    public String toString() {
        return "TokenX{" +
                "clientId='" + clientId + '\'' +
                ", jwk='[hidden]'" +
                ", wellKnownUrl='" + wellKnownUrl + '\'' +
                '}';
    }
}
