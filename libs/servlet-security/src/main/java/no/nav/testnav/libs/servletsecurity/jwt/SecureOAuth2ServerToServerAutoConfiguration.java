package no.nav.testnav.libs.servletsecurity.jwt;

import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import no.nav.testnav.libs.securitycore.texas.TexasJwtDecoder;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserJwt;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import static java.util.stream.Collectors.toMap;

@AutoConfiguration
@Import({
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenExchange.class,
        GetAuthenticatedResourceServerType.class,
        GetAuthenticatedToken.class,
        GetAuthenticatedId.class,
        GetUserInfo.class,
        GetUserJwt.class,
})
public class SecureOAuth2ServerToServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder(TexasClient texasClient, List<ResourceServerProperties> properties) {
        var decoder = new TexasJwtDecoder(texasClient, properties
                .stream()
                .collect(toMap(
                        ResourceServerProperties::getIssuerUri,
                        resourceServer -> resourceServer.getType() == ResourceServerType.TOKEN_X ? TexasClient.TOKENX : TexasClient.ENTRA_ID,
                        (first, second) -> first)));
        return token -> decoder.decode(token).block();
    }
}
