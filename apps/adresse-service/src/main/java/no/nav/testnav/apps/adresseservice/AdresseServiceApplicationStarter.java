package no.nav.testnav.apps.adresseservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import reactor.core.publisher.Hooks;

@EnableWebFluxSecurity
@SpringBootApplication
public class AdresseServiceApplicationStarter {

    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        new SpringApplicationBuilder(AdresseServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
