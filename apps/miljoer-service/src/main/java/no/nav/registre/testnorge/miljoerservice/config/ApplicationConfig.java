package no.nav.registre.testnorge.miljoerservice.config;

import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
public class ApplicationConfig {

}
