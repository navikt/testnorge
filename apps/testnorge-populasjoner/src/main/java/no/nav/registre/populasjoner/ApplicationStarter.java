package no.nav.registre.populasjoner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.populasjoner.vault.VaultUtil;

@Slf4j
@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        System.setProperty("app.name", "testnorge-populasjoner");
        System.setProperty("nais.namespace", "default");
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(ApplicationStarter.class, args);
    }
}