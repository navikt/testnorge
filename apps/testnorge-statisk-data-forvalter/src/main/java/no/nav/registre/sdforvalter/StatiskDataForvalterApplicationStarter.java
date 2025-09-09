package no.nav.registre.sdforvalter;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.nais.InitScripts;
import no.nav.dolly.libs.nais.init.SystemPropertyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StatiskDataForvalterApplicationStarter {
    public static void main(String[] args) {
        InitScripts.run(
                new SystemPropertyInitializer("SERVICEUSER_USERNAME", "/var/run/secrets/nais.io/serviceuser/password"),
                new SystemPropertyInitializer("SERVICEUSER_PASSWORD", "/secret/serviceuser/password"),
                new SystemPropertyInitializer("SPRING_CLOUD_VAULT_TOKEN", "/var/run/secrets/nais.io/vault/vault_token")
        );
        new SpringApplicationBuilder(StatiskDataForvalterApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
