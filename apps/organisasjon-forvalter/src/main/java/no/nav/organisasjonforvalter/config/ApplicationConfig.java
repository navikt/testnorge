package no.nav.organisasjonforvalter.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({CoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
@Configuration
public class ApplicationConfig {

}
