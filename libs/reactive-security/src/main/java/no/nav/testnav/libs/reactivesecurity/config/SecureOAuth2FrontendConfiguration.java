package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.service.AccessTokenService;
import no.nav.testnav.libs.reactivesecurity.service.SecureOAuth2AuthenticationTokenResolver;


@Configuration
@Import({
        SecureOAuth2AuthenticationTokenResolver.class,
        AccessTokenService.class,
        AzureClientCredentials.class
})
public class SecureOAuth2FrontendConfiguration {
}