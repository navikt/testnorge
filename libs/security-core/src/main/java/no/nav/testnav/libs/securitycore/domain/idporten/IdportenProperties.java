package no.nav.testnav.libs.securitycore.domain.idporten;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdportenProperties {

    private final String clientId;
    private final String jwk;
    private final String wellKnownUrl;

    public IdportenProperties(
            @Value("${IDPORTEN_CLIENT_ID:#{null}}") String clientId,
            @Value("${IDPORTEN_PRIVATE_JWK:#{null}}") String jwk,
            @Value("${IDPORTEN_WELL_KNOWN_URL:#{null}}") String wellKnownUrl
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
        return "IdportenX{" +
                "clientId='" + clientId + '\'' +
                ", jwk='[hidden]'" +
                ", wellKnownUrl='" + wellKnownUrl + '\'' +
                '}';
    }
}
