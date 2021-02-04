package no.nav.udistub.cloud;

import no.nav.udistub.cloud.config.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.udistub.ApplicationConfig;

@SpringBootApplication
public class CloudApplicationStarter {

    public static void main(String[] args) {
        VaultUtil.setCloudVaultToken();
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
