package no.nav.no.registere.testnorge.arbeidsforholdexportapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.DependencyAnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.AnalysisFSSAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        DependencyAnalysisAutoConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class,
        AnalysisFSSAutoConfiguration.class
})
public class AppConfig {
}
