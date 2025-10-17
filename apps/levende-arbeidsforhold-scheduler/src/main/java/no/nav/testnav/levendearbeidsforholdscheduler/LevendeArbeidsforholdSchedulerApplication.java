package no.nav.testnav.levendearbeidsforholdscheduler;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LevendeArbeidsforholdSchedulerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LevendeArbeidsforholdSchedulerApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}
