package no.nav.testnav.pdlpersonopensearchservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PdlPersonOpensearchServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PdlPersonOpensearchServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}