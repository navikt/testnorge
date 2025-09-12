package no.nav.brregstub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.nais.NaisVaultKeyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BrregApplicationLauncher {
    public static void main(String[] args) {
        NaisVaultKeyInitializer.run();
        new SpringApplicationBuilder(BrregApplicationLauncher.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
