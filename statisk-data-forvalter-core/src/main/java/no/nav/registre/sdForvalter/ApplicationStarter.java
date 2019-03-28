package no.nav.registre.sdForvalter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import no.nav.registre.sdForvalter.vault.VaultUtil;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
public class ApplicationStarter {

    public static void main(String[] args) {
        VaultUtil.initCloudVaultToken();
        SpringApplication.run(ApplicationStarter.class, args);
    }
}

