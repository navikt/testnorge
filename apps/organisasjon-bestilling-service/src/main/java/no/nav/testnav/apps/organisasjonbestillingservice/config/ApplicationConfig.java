package no.nav.testnav.apps.organisasjonbestillingservice.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@Import({CoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
@EnableJpaAuditing
public class ApplicationConfig {

}
