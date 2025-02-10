package no.nav.brregstub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.vault.VaultTokenApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class BrregApplicationLauncher {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BrregApplicationLauncher.class)
                .initializers(
                        new VaultTokenApplicationContextInitializer(),
                        new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
