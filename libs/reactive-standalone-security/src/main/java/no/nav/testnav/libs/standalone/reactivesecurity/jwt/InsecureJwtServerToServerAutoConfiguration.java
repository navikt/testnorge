package no.nav.testnav.libs.standalone.reactivesecurity.jwt;

import no.nav.testnav.libs.standalone.reactivesecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.properties.TokenXResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

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
    ReactiveJwtDecoder reactiveJwtDecoder(List<ResourceServerProperties> properties) {
        return new MultipleIssuersReactiveJwtDecoder(properties);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    ReactiveJwtDecoder reactiveJwtDecoderForTesting() {
        return new NoopReactiveJwtDecoder();
    }

}