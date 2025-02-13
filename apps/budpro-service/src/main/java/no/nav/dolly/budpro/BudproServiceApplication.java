package no.nav.dolly.budpro;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BudproServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BudproServiceApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
