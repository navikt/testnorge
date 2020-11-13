package no.nav.no.registere.testnorge.arbeidsforholdexportapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.DependencyAnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import({ApplicationCoreConfig.class, DependencyAnalysisAutoConfiguration.class})
public class AppConfig {
}
