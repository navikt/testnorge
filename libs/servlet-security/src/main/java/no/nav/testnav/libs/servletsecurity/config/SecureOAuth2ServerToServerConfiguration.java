package no.nav.testnav.libs.servletsecurity.config;

import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserJwt;
import no.nav.testnav.libs.servletsecurity.decoder.MultipleIssuersJwtDecoder;
import no.nav.testnav.libs.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.exchange.TokenXService;
import no.nav.testnav.libs.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

@Configuration
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
public class SecureOAuth2ServerToServerConfiguration {

    // TODO: There's also one in no.nav.testnav.libs.standalone.servletsecurity.config.SecureOAuth2ServerToServerConfiguration. testnav-ident-pool (and possibly others) includes both libraries. This should be fixed.
    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersJwtDecoder(properties);
    }

}