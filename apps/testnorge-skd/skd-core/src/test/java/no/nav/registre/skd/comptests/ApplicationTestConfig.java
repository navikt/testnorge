package no.nav.registre.skd.comptests;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Random;

@Configuration
public class ApplicationTestConfig {

    @Bean
    @Primary
    public Random randMock() {
        return mock(Random.class);
    }
}
