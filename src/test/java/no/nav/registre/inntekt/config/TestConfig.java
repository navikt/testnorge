package no.nav.registre.inntekt.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan("no.nav.registre.inntekt")
@Profile("test")
public class TestConfig {
}
