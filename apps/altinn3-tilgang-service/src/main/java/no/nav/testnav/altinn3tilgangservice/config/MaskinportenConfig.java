package no.nav.testnav.altinn3tilgangservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
@NoArgsConstructor
public class MaskinportenConfig {

    @Value("${MASKINPORTEN_CLIENT_ID}")
    private String maskinportenClientId;

    @Value("${MASKINPORTEN_CLIENT_JWK}")
    private String maskinportenClientJwk;

    @Value("${MASKINPORTEN_SCOPES}")
    private String maskinportenScopes;

    @Value("${MASKINPORTEN_WELL_KNOWN_URL}")
    private String maskinportenWellKnownUrl;
}