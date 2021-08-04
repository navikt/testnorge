package no.nav.testnav.libs.servletsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.testnav.libs.servletsecurity.service.ClientCredentialGenerateAccessTokenService;
import no.nav.testnav.libs.servletsecurity.service.SecureJwtAuthenticationTokenResolver;

@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        ClientCredentialGenerateAccessTokenService.class,
        AzureClientCredentials.class,
        AccessTokenService.class
})
public class SecureOAuth2ServerToServerConfiguration {
}