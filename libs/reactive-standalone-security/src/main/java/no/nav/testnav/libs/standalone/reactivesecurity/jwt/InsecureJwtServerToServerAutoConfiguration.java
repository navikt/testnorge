package no.nav.testnav.libs.standalone.reactivesecurity.jwt;

import no.nav.testnav.libs.securitycore.texas.TexasClient;
import no.nav.testnav.libs.securitycore.texas.TexasJwtDecoder;
import no.nav.testnav.libs.standalone.reactivesecurity.domain.ResourceServerType;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import java.util.List;

import static java.util.stream.Collectors.toMap;

@AutoConfiguration
@Import({
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenExchange.class
})
class InsecureJwtServerToServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ReactiveJwtDecoder reactiveJwtDecoder(TexasClient texasClient, List<ResourceServerProperties> properties) {
        return new TexasJwtDecoder(texasClient, properties
                .stream()
                .collect(toMap(
                        ResourceServerProperties::getIssuerUri,
                        resourceServer -> resourceServer.getType() == ResourceServerType.TOKEN_X ? TexasClient.TOKENX : TexasClient.ENTRA_ID,
                        (first, _) -> first)));
    }
}
