package no.nav.registre.testnorge.libs.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.InsecureAuthenticationTokenResolver;


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