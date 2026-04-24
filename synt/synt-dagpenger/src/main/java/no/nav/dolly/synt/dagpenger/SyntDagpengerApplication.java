package no.nav.dolly.synt.dagpenger;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SyntDagpengerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SyntDagpengerApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}
