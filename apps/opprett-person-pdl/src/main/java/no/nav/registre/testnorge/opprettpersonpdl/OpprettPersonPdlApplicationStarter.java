package no.nav.registre.testnorge.opprettpersonpdl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class OpprettPersonPdlApplicationStarter {
    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }
        SpringApplication.run(OpprettPersonPdlApplicationStarter.class, args);
    }
}
