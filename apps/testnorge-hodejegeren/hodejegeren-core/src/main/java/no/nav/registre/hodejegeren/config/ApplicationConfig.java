package no.nav.registre.hodejegeren.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Random;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@EnableAsync
@Import(ApplicationCoreConfig.class)
public class ApplicationConfig {

    @Bean
    public Random rand() {
        return new Random();
    }
}
