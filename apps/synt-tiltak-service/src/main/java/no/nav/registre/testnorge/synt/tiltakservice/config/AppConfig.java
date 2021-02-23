package no.nav.registre.testnorge.synt.tiltakservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.AnalysisGCPAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        AnalysisGCPAutoConfiguration.class
})
public class AppConfig {
}
