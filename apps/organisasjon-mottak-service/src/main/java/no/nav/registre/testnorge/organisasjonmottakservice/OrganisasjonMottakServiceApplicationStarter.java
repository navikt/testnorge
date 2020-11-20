package no.nav.registre.testnorge.organisasjonmottakservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@SpringBootApplication
public class OrganisasjonMottakServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(OrganisasjonMottakServiceApplicationStarter.class, args);
    }
}
