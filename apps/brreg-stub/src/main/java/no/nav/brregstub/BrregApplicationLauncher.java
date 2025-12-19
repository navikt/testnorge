package no.nav.brregstub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.vault.NaisVaultKeyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;

@SpringBootApplication(exclude = JacksonAutoConfiguration.class)
public class BrregApplicationLauncher {
    public static void main(String[] args) {
        NaisVaultKeyInitializer.run();
        new SpringApplicationBuilder(BrregApplicationLauncher.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
