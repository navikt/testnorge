package no.nav.registre.testnorge.libs.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.SecureOAuth2AuthenticationTokenResolver;

@Configuration
@Import({
        SecureOAuth2AuthenticationTokenResolver.class,
        AccessTokenService.class,
        AzureClientCredentials.class
})
public class SecureOAuth2FrontendConfiguration {
}