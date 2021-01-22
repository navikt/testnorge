package no.nav.registre.testnorge.hendelse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.testnorge.hendelse.util.VaultUtil;

@SpringBootApplication
public class HendelseApiApplicationStarter {
    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(HendelseApiApplicationStarter.class, args);
    }
}
