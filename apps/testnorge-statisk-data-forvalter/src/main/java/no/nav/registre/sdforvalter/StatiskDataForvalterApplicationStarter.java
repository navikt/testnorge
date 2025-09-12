package no.nav.registre.sdforvalter;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.nais.NaisFileIntoSystemPropertyInitializer;
import no.nav.dolly.libs.nais.NaisVaultKeyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StatiskDataForvalterApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(StatiskDataForvalterApplicationStarter.class)
                .initializers(
                        new NaisEnvironmentApplicationContextInitializer(),
                        new NaisFileIntoSystemPropertyInitializer("SERVICEUSER_USERNAME", "/var/run/secrets/nais.io/serviceuser/password"),
                        new NaisFileIntoSystemPropertyInitializer("SERVICEUSER_PASSWORD", "/secret/serviceuser/password"))
                .run(args);
    }
}
