package no.nav.testnav.libs.reactivesecurity.jwt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@AutoConfiguration
public class ReactiveJwtAutoConfiguration {

    @Primary
    @Bean
    @Profile("test")
    @ConditionalOnBean(ReactiveJwtDecoder.class)
    ReactiveJwtDecoder reactiveJwtDecoderForTesting() {
        return new NoopReactiveJwtDecoder();
    }

}
