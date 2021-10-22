package no.nav.testnav.libs.servletsecurity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserJwt;
import no.nav.testnav.libs.servletsecurity.decoder.MultipleIssuersJwtDecoder;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.domain.TokenX;
import no.nav.testnav.libs.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.exchange.TokenXService;
import no.nav.testnav.libs.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.testnav.libs.servletsecurity.service.ClientCredentialGenerateAccessTokenService;
import no.nav.testnav.libs.servletsecurity.service.SecureJwtAuthenticationTokenResolver;

@Configuration
@Import({
        SecureJwtAuthenticationTokenResolver.class,
        ClientCredentialGenerateAccessTokenService.class,
        AzureClientCredentials.class,
        AccessTokenService.class,
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenXService.class,
        AzureAdTokenService.class,
        TokenExchange.class,
        GetAuthenticatedResourceServerType.class,
        GetAuthenticatedToken.class,
        GetAuthenticatedId.class,
        TokenX.class,
        GetUserInfo.class,
        GetUserJwt.class
})
public class SecureOAuth2ServerToServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersJwtDecoder(properties);
    }

}