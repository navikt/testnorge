package no.nav.registre.varslingerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.testnav.libs.servletcore.util.VaultUtil;

@SpringBootApplication
public class VarslingerServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(VarslingerServiceApplicationStarter.class, args);
    }
}
