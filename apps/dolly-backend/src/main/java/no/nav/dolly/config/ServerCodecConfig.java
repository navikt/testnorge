package no.nav.dolly.config;

import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class ServerCodecConfig implements WebFluxConfigurer {

    private static final int MAX_IN_MEMORY_SIZE = 50 * 1024 * 1024;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }

    @Bean
    CodecCustomizer serverCodecCustomizer() {
        return configurer -> configurer.defaultCodecs()
                .maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }
}
