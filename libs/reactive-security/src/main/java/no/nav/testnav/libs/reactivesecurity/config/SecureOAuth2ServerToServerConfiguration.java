package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.service.AccessTokenService;
import no.nav.testnav.libs.reactivesecurity.service.SecureJwtAuthenticationTokenResolver;


@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        AzureClientCredentials.class,
        AccessTokenService.class
})
public class SecureOAuth2ServerToServerConfiguration {
}