package no.nav.testnav.apps.adresseservice;

import no.nav.testnav.libs.servletcore.config.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AdresseServiceApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AdresseServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}