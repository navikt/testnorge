package no.nav.identpool;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentPoolApplicationStarter {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(IdentPoolApplicationStarter.class, args);
    }
}
