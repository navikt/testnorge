package no.nav.registre.testnorge.organisasjonmottakservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@SpringBootApplication
public class OrganisasjonMottakServiceApplicationStarter {
    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(OrganisasjonMottakServiceApplicationStarter.class, args);
    }
}
