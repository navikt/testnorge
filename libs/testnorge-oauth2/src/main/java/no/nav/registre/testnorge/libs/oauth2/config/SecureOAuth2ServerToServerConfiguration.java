package no.nav.registre.testnorge.libs.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.SecureJwtAuthenticationTokenResolver;

@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        ClientCredentialGenerateAccessTokenService.class,
        AzureClientCredentials.class,
        AccessTokenService.class
})
public class SecureOAuth2ServerToServerConfiguration {
}