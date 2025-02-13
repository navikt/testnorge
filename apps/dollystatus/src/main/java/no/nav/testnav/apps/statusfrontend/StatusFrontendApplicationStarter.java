package no.nav.testnav.apps.statusfrontend;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StatusFrontendApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(StatusFrontendApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}
