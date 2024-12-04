package no.nav.brregstub;

import no.nav.testnav.libs.vault.VaultUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BrregApplicationLauncher {

    public static void main(String[] args) {
        VaultUtils.initCloudVaultToken("prod");
        SpringApplication.run(BrregApplicationLauncher.class, args);
    }

}
