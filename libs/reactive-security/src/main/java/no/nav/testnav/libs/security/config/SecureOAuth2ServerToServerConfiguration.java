package no.nav.testnav.libs.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.security.domain.AzureClientCredentials;
import no.nav.testnav.libs.security.service.AccessTokenService;
import no.nav.testnav.libs.security.service.SecureJwtAuthenticationTokenResolver;


@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        AzureClientCredentials.class,
        AccessTokenService.class
})
public class SecureOAuth2ServerToServerConfiguration {
}