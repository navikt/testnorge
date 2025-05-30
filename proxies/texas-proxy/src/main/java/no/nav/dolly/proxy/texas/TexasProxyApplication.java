package no.nav.dolly.proxy.texas;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableWebFluxSecurity
public class TexasProxyApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TexasProxyApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}
