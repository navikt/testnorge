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
import no.nav.testnav.libs.reactivesecurity.domain.AzureNavClientCredential;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.domain.AzureTrygdeetatenClientCredential;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;

@Configuration
@Import({
        AzureNavClientCredential.class,
        TokenXService.class,
        TokenxResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        AzureAdTokenService.class,
        TokenExchange.class,
        GetAuthenticatedUserId.class,
        GetAuthenticatedResourceServerType.class,
        GetAuthenticatedToken.class,
        TokenX.class,
        AzureTrygdeetatenClientCredential.class,
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