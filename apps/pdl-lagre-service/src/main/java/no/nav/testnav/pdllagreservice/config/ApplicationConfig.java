package no.nav.testnav.pdllagreservice.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration
@Import({CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class})
public class ApplicationConfig {

}