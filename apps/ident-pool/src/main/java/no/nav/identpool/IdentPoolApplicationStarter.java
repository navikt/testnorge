package no.nav.identpool;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.servletcore.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class IdentPoolApplicationStarter {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            log.info("Starter lesing av Vault token ...");
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(IdentPoolApplicationStarter.class, args);
    }

}
