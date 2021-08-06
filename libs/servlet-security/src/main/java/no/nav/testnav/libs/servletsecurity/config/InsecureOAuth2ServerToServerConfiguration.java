package no.nav.testnav.libs.servletsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
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
        AzureClientCredentials.class
})
public class InsecureOAuth2ServerToServerConfiguration {
}