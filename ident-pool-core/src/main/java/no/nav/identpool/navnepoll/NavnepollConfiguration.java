package no.nav.identpool.navnepoll;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NavnepollConfiguration {

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
