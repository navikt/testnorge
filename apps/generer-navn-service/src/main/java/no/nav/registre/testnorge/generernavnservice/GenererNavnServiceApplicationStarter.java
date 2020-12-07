package no.nav.registre.testnorge.generernavnservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@SpringBootApplication
public class GenererNavnServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(GenererNavnServiceApplicationStarter.class, args);
    }
}