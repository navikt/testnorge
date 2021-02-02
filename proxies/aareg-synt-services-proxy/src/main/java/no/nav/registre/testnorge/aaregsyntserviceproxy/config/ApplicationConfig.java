package no.nav.registre.testnorge.aaregsyntserviceproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.core.config.AnalysisFSSAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        AnalysisFSSAutoConfiguration.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {
}
