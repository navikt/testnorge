package no.nav.testnav.libs.reactivesecurity.config;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TrygdeetatenAzureAdResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@Import({
        TokenXService.class,
        TokenxResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TrygdeetatenAzureAdResourceServerProperties.class,
        TokenExchange.class,
        GetAuthenticatedUserId.class,
        GetAuthenticatedResourceServerType.class,
        GetAuthenticatedToken.class,
        TokenXProperties.class
})
public class SecureOAuth2ServerToServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(
            WebClient webClient,
            List<ResourceServerProperties> resourceServerProperties,
            @Value("${HTTP_PROXY:#{null}}") String proxyHost
    ) {
        return new JwtReactiveAuthenticationManager(webClient, resourceServerProperties, proxyHost);
    }
}