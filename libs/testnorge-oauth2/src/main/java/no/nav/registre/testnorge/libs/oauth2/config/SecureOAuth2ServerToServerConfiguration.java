package no.nav.registre.testnorge.libs.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.SecureAuthenticationTokenResolver;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;

@Configuration
@Import({
        AccessTokenService.class,
        SecureAuthenticationTokenResolver.class,
        ClientCredentialGenerateAccessTokenService.class,
        OnBehalfOfGenerateAccessTokenService.class
})
public class SecureOAuth2ServerToServerConfiguration {
}