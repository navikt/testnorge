package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.service.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesecurity.service.SecureJwtAuthenticationTokenResolver;
import no.nav.testnav.libs.reactivesecurity.service.TokenExchange;

@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        AzureClientCredentials.class,
        TokenExchange.class,
        TokenX.class
})
public class SecureOAuth2ServerToServerConfiguration {
}