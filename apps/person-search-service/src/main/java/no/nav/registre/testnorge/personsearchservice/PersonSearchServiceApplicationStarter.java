package no.nav.registre.testnorge.personsearchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@SpringBootApplication
public class PersonSearchServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(PersonSearchServiceApplicationStarter.class, args);
    }
}
