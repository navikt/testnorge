package no.nav.registre.testnorge.sykemelding;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "no.nav.registre.testnorge.sykemelding")
@Import({ ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
})
public class SykemeldingApiApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(SykemeldingApiApplicationStarter.class, args);
    }
}
