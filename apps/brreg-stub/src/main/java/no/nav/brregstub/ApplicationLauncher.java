package no.nav.brregstub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.brregstub.config.VaultUtil;

@SpringBootApplication
public class ApplicationLauncher {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.setCloudVaultToken();
        }
        SpringApplication.run(ApplicationLauncher.class, args);
    }
}
