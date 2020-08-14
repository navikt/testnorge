package no.nav.registre.udistub.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.udistub.ApplicationConfig;
import no.nav.registre.udistub.cloud.config.VaultUtil;

@SpringBootApplication
public class CloudApplicationStarter {

    public static void main(String[] args) {
        VaultUtil.setCloudVaultToken();
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
