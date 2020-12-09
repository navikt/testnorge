package no.nav.organisasjonforvalter;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
