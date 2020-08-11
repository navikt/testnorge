package no.nav.registre.testnorge.rapportering;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registere.testnorge.core.util.VaultUtil;

@SpringBootApplication
public class RapporteringApiApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }
        SpringApplication.run(RapporteringApiApplicationStarter.class, args);
    }

}
