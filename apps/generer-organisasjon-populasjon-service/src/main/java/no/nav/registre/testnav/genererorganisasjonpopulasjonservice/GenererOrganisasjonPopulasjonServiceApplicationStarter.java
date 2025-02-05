package no.nav.registre.testnav.genererorganisasjonpopulasjonservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GenererOrganisasjonPopulasjonServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GenererOrganisasjonPopulasjonServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
