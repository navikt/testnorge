package no.nav.testnav.dollysearchservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class
DollySearchServiceApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder(DollySearchServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
