package no.nav.dolly.proxy.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DokarkivUploadCacheConfig {

    @Bean
    public Cache<String, StringBuilder> dokarkivUploadCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30))
                .maximumSize(100)
                .build();
    }

}
