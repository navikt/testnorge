package no.nav.registre.testnorge.generernavnservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GenererNavnServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GenererNavnServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}