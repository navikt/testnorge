package no.nav.registre.inntekt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.inntekt.vault.VaultUtil;

@Slf4j
@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }
        log.info(System.getProperty("spring.profiles.active"));
        log.info(System.getProperty("spring.cloud.vault.token"));
        SpringApplication.run(ApplicationStarter.class, args);
    }
}