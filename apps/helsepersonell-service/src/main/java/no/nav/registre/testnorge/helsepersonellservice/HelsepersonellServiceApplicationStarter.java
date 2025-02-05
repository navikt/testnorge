package no.nav.registre.testnorge.helsepersonellservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class HelsepersonellServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(HelsepersonellServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
