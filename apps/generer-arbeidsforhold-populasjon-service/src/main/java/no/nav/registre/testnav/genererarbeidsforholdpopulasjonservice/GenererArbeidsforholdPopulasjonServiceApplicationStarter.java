package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GenererArbeidsforholdPopulasjonServiceApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GenererArbeidsforholdPopulasjonServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}