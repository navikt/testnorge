package no.nav.registre.testnorge.organisasjonfastedataservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class OrganisasjonFasteDataServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OrganisasjonFasteDataServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}