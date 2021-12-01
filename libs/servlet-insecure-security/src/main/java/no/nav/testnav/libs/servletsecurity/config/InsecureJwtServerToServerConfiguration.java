package no.nav.testnav.libs.servletsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.servletsecurity.decoder.MultipleIssuersJwtDecoder;
import no.nav.testnav.libs.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;

@Configuration
@Import({
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenExchange.class,
        AzureAdTokenService.class,
        AzureNavClientCredential.class
})
public class InsecureJwtServerToServerConfiguration {
    @Bean
    public JwtDecoder jwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersJwtDecoder(properties);
    }
}