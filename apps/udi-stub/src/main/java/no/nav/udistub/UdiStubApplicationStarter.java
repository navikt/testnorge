package no.nav.udistub;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.vault.VaultUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class UdiStubApplicationStarter {
    public static void main(String[] args) {
        VaultUtils.initCloudVaultToken("prod");
        new SpringApplicationBuilder(UdiStubApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
