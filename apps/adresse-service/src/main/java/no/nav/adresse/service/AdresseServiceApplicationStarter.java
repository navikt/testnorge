package no.nav.tpsidenter.vedlikehold;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdresseServiceApplicationStarter {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(AdresseServiceApplicationStarter.class, args);
    }
}
