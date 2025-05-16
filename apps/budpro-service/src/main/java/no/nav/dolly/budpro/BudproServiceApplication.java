package no.nav.dolly.budpro;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableWebFluxSecurity
public class BudproServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BudproServiceApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
