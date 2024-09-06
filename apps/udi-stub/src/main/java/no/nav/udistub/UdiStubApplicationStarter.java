package no.nav.udistub;

import no.nav.testnav.libs.servletcore.util.VaultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UdiStubApplicationStarter {

    public static void main(String[] args) {

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(UdiStubApplicationStarter.class, args);
    }
}
