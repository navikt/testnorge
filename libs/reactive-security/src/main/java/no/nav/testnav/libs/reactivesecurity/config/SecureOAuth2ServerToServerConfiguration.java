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
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerType;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerTypeAction;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedTokenAction;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserIdAction;

@Configuration
@Import({
        AzureClientCredentials.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        TokenxResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        GetAuthenticatedUserIdAction.class,
        GetAuthenticatedResourceServerTypeAction.class,
        GetAuthenticatedTokenAction.class,
        TokenX.class
})
public class SecureOAuth2ServerToServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TokenExchange tokenExchange(
            GetAuthenticatedResourceServerTypeAction getAuthenticatedResourceServerType,
            AzureAdTokenExchange azureAdTokenExchange,
            TokenXExchange tokenXExchange
    ) {
        var tokenExchange = new TokenExchange(getAuthenticatedResourceServerType);
        tokenExchange.addExchange(ResourceServerType.AZURE_AD, azureAdTokenExchange);
        tokenExchange.addExchange(ResourceServerType.TOKEN_X, tokenXExchange);
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