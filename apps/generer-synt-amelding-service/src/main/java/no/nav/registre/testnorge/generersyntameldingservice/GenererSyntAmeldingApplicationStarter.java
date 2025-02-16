package no.nav.registre.testnorge.generersyntameldingservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GenererSyntAmeldingApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GenererSyntAmeldingApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
