package no.nav.brregstub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.vault.VaultUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class BrregApplicationLauncher {

    public static void main(String[] args) {
        VaultUtils.initCloudVaultToken("prod");
        new SpringApplicationBuilder(BrregApplicationLauncher.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}
