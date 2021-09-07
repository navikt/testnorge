package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.service.AuthenticationTokenResolver;
import no.nav.testnav.libs.reactivesecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesecurity.service.SecureOAuth2AuthenticationTokenResolver;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenXExchange;

@Configuration
@Import({
        SecureOAuth2AuthenticationTokenResolver.class,
        AzureClientCredentials.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        TokenX.class
})
public class SecureOAuth2FrontendConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TokenExchange tokenExchange(
            AuthenticationTokenResolver tokenResolver,
            AzureAdTokenExchange azureAdTokenExchange,
            TokenXExchange tokenXExchange
    ) {
        var tokenExchange = new TokenExchange(tokenResolver);
        tokenExchange.addExchange("aad", azureAdTokenExchange);
        tokenExchange.addExchange("idporten", tokenXExchange);
        return tokenExchange;
    }


}