package no.nav.testnav.libs.reactivesecurity.config;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TrygdeetatenAzureAdResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import no.nav.testnav.libs.securitycore.texas.TexasJwtDecoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import java.util.List;

import static java.util.stream.Collectors.toMap;

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
        GetUserInfo.class
})
public class SecureOAuth2ServerToServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReactiveJwtDecoder texasJwtDecoder(TexasClient texasClient, List<ResourceServerProperties> resourceServerProperties) {
        return new TexasJwtDecoder(texasClient, resourceServerProperties
                .stream()
                .collect(toMap(
                        ResourceServerProperties::getIssuerUri,
                        properties -> properties.getType() == ResourceServerType.TOKEN_X ? TexasClient.TOKENX : TexasClient.ENTRA_ID,
                        (first, _) -> first)));
    }
}
