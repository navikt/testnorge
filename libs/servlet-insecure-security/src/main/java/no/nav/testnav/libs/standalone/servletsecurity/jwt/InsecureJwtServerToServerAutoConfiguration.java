package no.nav.testnav.libs.standalone.servletsecurity.jwt;

import no.nav.testnav.libs.standalone.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.servletsecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

@AutoConfiguration
@Import({
        TokenXResourceServerProperties.class,
        AzureAdResourceServerProperties.class,
        TokenExchange.class,
        AzureAdTokenService.class
})
class InsecureJwtServerToServerAutoConfiguration {

    @Bean
    @Profile("!test")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("no.nav.testnav.libs.servletsecurity.jwt.MultipleIssuersJwtDecoder")
    JwtDecoder jwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersJwtDecoder(properties);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("no.nav.testnav.libs.servletsecurity.jwt.NoopJwtDecoder")
    JwtDecoder jwtDecoderForTesting() {
        return new NoopJwtDecoder();
    }

}