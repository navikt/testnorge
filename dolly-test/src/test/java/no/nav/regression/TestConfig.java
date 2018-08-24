package no.nav.regression;

import no.nav.dolly.ApplicationStarter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationStarter.class)
public class TestConfig {
}
