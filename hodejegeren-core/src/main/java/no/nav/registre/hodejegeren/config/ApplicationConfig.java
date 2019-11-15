package no.nav.registre.hodejegeren.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Random;

@Configuration
@EnableAsync
public class ApplicationConfig {

    @Bean
    public Random rand() {
        return new Random();
    }
}
