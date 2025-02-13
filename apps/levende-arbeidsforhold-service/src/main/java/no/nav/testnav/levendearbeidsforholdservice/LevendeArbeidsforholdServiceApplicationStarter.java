package no.nav.testnav.levendearbeidsforholdservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LevendeArbeidsforholdServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(LevendeArbeidsforholdServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}