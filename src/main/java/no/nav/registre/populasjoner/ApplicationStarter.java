package no.nav.registre.populasjoner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.populasjoner.vault.VaultUtil;

@Slf4j
@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        } else {
            System.setProperty("spring.cloud.vault.token", "00000000-0000-0000-0000-000000000000");
        }
        SpringApplication.run(ApplicationStarter.class, args);
    }
}