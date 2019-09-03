package no.nav.registre.udistub.cloud;

import no.nav.registre.udistub.ApplicationConfig;
import no.nav.registre.udistub.cloud.config.VaultUtil;
import org.springframework.boot.SpringApplication;

public class CloudApplicationStarter {

    public static void main(String[] args) {
        VaultUtil.setCloudVaultToken();
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
