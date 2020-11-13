package no.nav.registre.testnorge.libs.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.SecureOAuth2AuthenticationTokenResolver;

@Configuration
@Import({
        SecureOAuth2AuthenticationTokenResolver.class,
        OnBehalfOfGenerateAccessTokenService.class
})
public class SecureOAuth2FrontendConfiguration {
}