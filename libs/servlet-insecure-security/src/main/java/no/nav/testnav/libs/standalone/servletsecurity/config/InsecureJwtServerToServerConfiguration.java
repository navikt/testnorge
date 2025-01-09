package no.nav.testnav.libs.standalone.servletsecurity.config;

import no.nav.testnav.libs.standalone.servletsecurity.decoder.MultipleIssuersJwtDecoder;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

@Configuration
@Import({
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenExchange.class,
        AzureAdTokenService.class
})
public class InsecureJwtServerToServerConfiguration {

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
        return token -> null;
    }

}