package no.nav.registre.testnorge.mn.personservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import(value = {ApplicationCoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
public class ApplicationConfig {

}
