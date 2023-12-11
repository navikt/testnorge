package no.nav.registre.sdforvalter;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class StatiskDataForvalterApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(StatiskDataForvalterApplicationStarter.class, args);
    }
}