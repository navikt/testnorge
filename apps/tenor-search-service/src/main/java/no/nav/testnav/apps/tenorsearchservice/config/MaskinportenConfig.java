package no.nav.testnav.apps.tenorsearchservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MaskinportenConfig {

    @Value("${MASKINPORTEN_CLIENT_ID}")
    private String clientId;

    @Value("${MASKINPORTEN_CLIENT_JWK}")
    private String jwkPrivate;

    @Value("${MASKINPORTEN_SCOPES}")
    private String scope;

    @Value("${MASKINPORTEN_WELL_KNOWN_URL}")
    private String wellKnownUrl;
}
