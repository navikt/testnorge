package no.nav.registre.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.cloud.config.VaultUtil;

@SpringBootApplication(scanBasePackages = {"no.nav.registre.core", "no.nav.registre.cloud"})
public class CloudApplicationStarter {

    public static void main(String[] args) {
        VaultUtil.initCloudVaultToken();
        SpringApplication.run(CloudApplicationStarter.class, args);
    }

}
