package no.nav.udistub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.vault.NaisVaultKeyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class UdiStubApplicationStarter {
    public static void main(String[] args) {
        NaisVaultKeyInitializer.run();
        new SpringApplicationBuilder(UdiStubApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
