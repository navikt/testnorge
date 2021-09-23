package no.nav.testnav.libs.servletsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import no.nav.testnav.libs.servletsecurity.decoder.MultipleIssuersJwtDecoder;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.testnav.libs.servletsecurity.service.ClientCredentialGenerateAccessTokenService;
import no.nav.testnav.libs.servletsecurity.service.InsecureAuthenticationTokenResolver;


/**
 * Skal kun brukes til kafka apper som ikke har rest endepunker og apper utenfor GCP
 */
@Configuration
@Import({
        InsecureAuthenticationTokenResolver.class,
        ClientCredentialGenerateAccessTokenService.class,
        AccessTokenService.class,
        AzureClientCredentials.class,
        AzureAdResourceServerProperties.class
})
public class InsecureOAuth2ServerToServerConfiguration {

    @Bean
    public JwtDecoder jwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersJwtDecoder(properties);
    }
}