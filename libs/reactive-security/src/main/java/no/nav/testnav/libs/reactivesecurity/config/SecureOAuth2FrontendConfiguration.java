package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.service.SecureOAuth2AuthenticationTokenResolver;
import no.nav.testnav.libs.reactivesecurity.service.TokenExchange;

@Configuration
@Import({
        SecureOAuth2AuthenticationTokenResolver.class,
        TokenExchange.class,
        AzureClientCredentials.class,
        TokenX.class
})
public class SecureOAuth2FrontendConfiguration {
}