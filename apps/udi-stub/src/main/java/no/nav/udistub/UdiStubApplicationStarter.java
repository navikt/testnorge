package no.nav.udistub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.nais.InitScripts;
import no.nav.dolly.libs.nais.init.SystemPropertyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class UdiStubApplicationStarter {
    public static void main(String[] args) {
        InitScripts.run(new SystemPropertyInitializer("SPRING_CLOUD_VAULT_TOKEN", "/var/run/secrets/nais.io/vault/vault_token"));
        new SpringApplicationBuilder(UdiStubApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
