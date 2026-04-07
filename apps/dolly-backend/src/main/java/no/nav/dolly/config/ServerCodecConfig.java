package no.nav.dolly.config;

import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerCodecConfig {

    private static final int MAX_IN_MEMORY_SIZE = 50 * 1024 * 1024;

    @Bean
    CodecCustomizer serverCodecCustomizer() {
        return configurer -> configurer.defaultCodecs()
                .maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }
}
