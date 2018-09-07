package no.nav.identpool.navnepool;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NavnepoolConfiguration {

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
