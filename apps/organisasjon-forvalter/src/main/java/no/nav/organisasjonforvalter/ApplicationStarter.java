package no.nav.organisasjonforvalter;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;

@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            Map<String, Object> properties = PropertyReader.builder()
                    .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                    .build();
        }
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
