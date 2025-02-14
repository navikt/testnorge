package no.nav.registre.testnorge.arbeidsforholdservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ArbeidsforholdApiApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ArbeidsforholdApiApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
