package no.nav.registre.sdforvalter;

import no.nav.testnav.libs.vault.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatiskDataForvalterApplicationStarter {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }
        SpringApplication.run(StatiskDataForvalterApplicationStarter.class, args);

    }
}