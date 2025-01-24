package no.nav.testnav.libs.servletsecurity.jwt;

import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import no.nav.testnav.libs.servletsecurity.action.*;
import no.nav.testnav.libs.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.exchange.TokenXService;
import no.nav.testnav.libs.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

@AutoConfiguration
@Import({
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenXService.class,
        AzureAdTokenService.class,
        TokenExchange.class,
        GetAuthenticatedResourceServerType.class,
        GetAuthenticatedToken.class,
        GetAuthenticatedId.class,
        TokenXProperties.class,
        GetUserInfo.class,
        GetUserJwt.class,
})
public class SecureOAuth2ServerToServerAutoConfiguration {

    @Bean
    @Profile("!test")
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersJwtDecoder(properties);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoderForTesting() {
        return new NoopJwtDecoder();
    }

}