package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.service.AuthenticationTokenResolver;
import no.nav.testnav.libs.reactivesecurity.service.SecureJwtAuthenticationTokenResolver;

@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        AzureClientCredentials.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        TokenxResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenX.class
})
public class SecureOAuth2ServerToServerConfiguration {

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

    @Bean
    @ConditionalOnMissingBean
    public JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(
            List<ResourceServerProperties> resourceServerProperties,
            @Value("${http.proxy:#{null}}") String proxyHost
    ) {
        return new JwtReactiveAuthenticationManager(resourceServerProperties, proxyHost);
    }
}