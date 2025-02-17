package no.nav.udistub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.vault.VaultTokenApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;

@SpringBootApplication
public class UdiStubApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UdiStubApplicationStarter.class)
                .initializers(
                        new VaultTokenApplicationContextInitializer(),
                        new NaisEnvironmentApplicationContextInitializer()
                )
                .listeners(event -> {
                    if (event instanceof ApplicationEnvironmentPreparedEvent) {
                        System.out.println("ApplicationEnvironmentPreparedEvent received");
                    }
                })
                .run(args);
    }
}
