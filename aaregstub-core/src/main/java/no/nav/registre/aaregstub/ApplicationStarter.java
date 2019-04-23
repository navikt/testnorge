package no.nav.registre.aaregstub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import no.nav.registre.aaregstub.vault.VaultUtil;

@SpringBootApplication
@EnableJpaRepositories
public class ApplicationStarter {

    public static void main(String[] args) {
        VaultUtil.initCloudVaultToken();
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
