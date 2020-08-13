package no.nav.brregstub;

import no.nav.brregstub.config.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {

    public static void main(String... args) {

        VaultUtil.setCloudVaultToken();
        SpringApplication.run(ApplicationConfig.class, args);
    }

}
