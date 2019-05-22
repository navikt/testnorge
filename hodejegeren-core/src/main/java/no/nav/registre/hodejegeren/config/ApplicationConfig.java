package no.nav.registre.hodejegeren.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ApplicationConfig {

    @Bean
    public Random rand() {
        return new Random();
    }
}
