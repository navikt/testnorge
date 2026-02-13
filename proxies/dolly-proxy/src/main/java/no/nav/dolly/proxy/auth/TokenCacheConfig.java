package no.nav.dolly.proxy.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TokenCacheConfig {

    @Value("${app.token-cache.grace-period-seconds:120}")
    private long gracePeriodSeconds;

    @Bean
    TokenCache tokenCacheService() {
        return new TokenCache(gracePeriodSeconds);
    }

}
