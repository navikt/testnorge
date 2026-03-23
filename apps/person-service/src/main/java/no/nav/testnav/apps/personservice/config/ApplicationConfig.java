package no.nav.testnav.apps.personservice.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Import({CoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
@EnableAsync
public class ApplicationConfig {

}
