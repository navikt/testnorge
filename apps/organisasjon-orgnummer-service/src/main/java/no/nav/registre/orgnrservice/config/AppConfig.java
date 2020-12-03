package no.nav.registre.orgnrservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import({
        ApplicationCoreConfig.class,
//        SecureOAuth2ServerToServerConfiguration.class,
//        DependencyAnalysisAutoConfiguration.class
})
public class AppConfig {

}
