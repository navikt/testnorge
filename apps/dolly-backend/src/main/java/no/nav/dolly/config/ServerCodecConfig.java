package no.nav.dolly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Slf4j
@Configuration
public class ServerCodecConfig implements WebFluxConfigurer {

    private static final int MAX_IN_MEMORY_SIZE = 50 * 1024 * 1024;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        log.info("Configuring server codec maxInMemorySize to {} bytes ({} MB)", MAX_IN_MEMORY_SIZE, MAX_IN_MEMORY_SIZE / 1024 / 1024);
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }

    @Bean
    CodecCustomizer serverCodecCustomizer() {
        return configurer -> configurer.defaultCodecs()
                .maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }
}
