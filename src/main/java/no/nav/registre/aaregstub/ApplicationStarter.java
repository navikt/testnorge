package no.nav.registre.aaregstub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.registre.aaregstub.vault.VaultUtil;

@Slf4j
@SpringBootApplication(exclude = {
})
public class ApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        } else {
            System.setProperty("spring.cloud.vault.token", "00000000-0000-0000-0000-000000000000");
        }
        log.info(System.getProperty("spring.profiles.active"));
        log.info(System.getProperty("spring.cloud.vault.token"));
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
