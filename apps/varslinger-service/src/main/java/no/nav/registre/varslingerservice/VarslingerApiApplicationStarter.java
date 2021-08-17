package no.nav.registre.varslingerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.testnav.libs.servletcore.util.VaultUtil;

@SpringBootApplication
public class VarslingerApiApplicationStarter {
    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(VarslingerApiApplicationStarter.class, args);
    }
}
