package no.nav.registre.testnorge.sykemelding.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "prod", "dev" })
public class AppConfig {

}
