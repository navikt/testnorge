package no.nav.dolly.regression;

import no.nav.dolly.ApplicationStarter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationStarter.class)
@ComponentScan(basePackageClasses = TestConfig.class)
public class TestConfig {
}
