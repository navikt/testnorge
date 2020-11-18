package no.nav.registre.orgnrservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.DependencyAnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
//        SecureOAuth2ServerToServerConfiguration.class,
//        DependencyAnalysisAutoConfiguration.class
})
public class AppConfig {

}
