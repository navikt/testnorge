package no.nav.registre.testnorge.mn.syntarbeidsforholdservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;


@SpringBootApplication
@EnableCaching
public class MNSyntArbeidsforholdServiceApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(MNSyntArbeidsforholdServiceApplicationStarter.class, args);
    }
}
