package no.nav.udistub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.nais.NaisFileIntoSystemPropertyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class UdiStubApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UdiStubApplicationStarter.class)
                .initializers(
                        new NaisEnvironmentApplicationContextInitializer(),
                        new NaisFileIntoSystemPropertyInitializer("SPRING_CLOUD_VAULT_TOKEN", "/var/run/secrets/nais.io/vault/vault_token"))
                .run(args);
    }
}
