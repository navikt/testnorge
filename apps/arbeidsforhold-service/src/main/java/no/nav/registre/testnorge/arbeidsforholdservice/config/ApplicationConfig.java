package no.nav.registre.testnorge.arbeidsforholdservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Slf4j
@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class ApplicationConfig {
}
