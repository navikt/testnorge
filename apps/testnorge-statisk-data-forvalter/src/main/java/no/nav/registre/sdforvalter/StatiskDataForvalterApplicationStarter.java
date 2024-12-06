package no.nav.registre.sdforvalter;

import no.nav.testnav.libs.vault.VaultUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatiskDataForvalterApplicationStarter {

    public static void main(String[] args) {
        VaultUtils.initCloudVaultToken("prod");
        SpringApplication.run(StatiskDataForvalterApplicationStarter.class, args);

    }

}