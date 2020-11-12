package no.nav.registre.testnorge.libs.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.DependencyAnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.config.LoggingWebConfig;
import no.nav.registre.testnorge.libs.core.provider.InternalController;
import no.nav.registre.testnorge.libs.dependencyanalysis.provider.DependenciesController;

@Configuration
@Import({
        InternalController.class,
        DependenciesController.class,
        ApplicationProperties.class,
        LoggingWebConfig.class,
        DependencyAnalysisAutoConfiguration.class
})
public class ApplicationCoreConfig {
}