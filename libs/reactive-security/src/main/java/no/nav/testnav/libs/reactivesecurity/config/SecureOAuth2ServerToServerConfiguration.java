package no.nav.testnav.libs.reactivesecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenXService;
import no.nav.testnav.libs.reactivesecurity.exchange.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;

@Configuration
@Import({
        AzureClientCredentials.class,
        TokenXService.class,
        TokenxResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        AzureAdTokenService.class,
        TokenExchange.class,
        GetAuthenticatedUserId.class,
        GetAuthenticatedResourceServerType.class,
        GetAuthenticatedToken.class,
        TokenX.class,
        TrygdeetatenConfig.class,
        TrygdeetatenAzureAdTokenService.class
})
public class SecureOAuth2ServerToServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(
            List<ResourceServerProperties> resourceServerProperties,
            @Value("${http.proxy:#{null}}") String proxyHost
    ) {
        return new JwtReactiveAuthenticationManager(resourceServerProperties, proxyHost);
    }
}