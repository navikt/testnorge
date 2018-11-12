package no.nav.registre.hodejegeren;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public Random rand() {
        return new Random();
    }
}
