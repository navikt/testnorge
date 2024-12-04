package no.nav.udistub;

import no.nav.testnav.libs.vault.VaultUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UdiStubApplicationStarter {

    public static void main(String[] args) {
        VaultUtils.initCloudVaultToken("prod");
        SpringApplication.run(UdiStubApplicationStarter.class, args);
    }

}
