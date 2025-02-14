package no.nav.registre.testnorge.organisasjonservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class OrganisasjonServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OrganisasjonServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}