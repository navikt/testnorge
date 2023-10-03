package no.nav.registre.testnorge.sykemelding.config;

import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "prod", "dev" })
@Import({ SecureOAuth2ServerToServerConfiguration.class })
public class AppConfig {

}
