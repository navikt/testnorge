package no.nav.registre.hodejegeren.comptests;

import static org.mockito.Mockito.mock;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationTestConfig {

    @Bean
    @Primary
    public Random randMock() {
        return mock(Random.class);
    }
}
