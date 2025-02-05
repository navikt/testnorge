package no.nav.registre.sdforvalter;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.vault.VaultUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StatiskDataForvalterApplicationStarter {
    public static void main(String[] args) {
        VaultUtils.initCloudVaultToken("prod");
        new SpringApplicationBuilder(StatiskDataForvalterApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}