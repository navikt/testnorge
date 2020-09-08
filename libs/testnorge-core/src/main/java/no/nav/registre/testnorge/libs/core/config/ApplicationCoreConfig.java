package no.nav.registre.testnorge.libs.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.dependencyanalysis.provider.DependenciesController;
import no.nav.registre.testnorge.libs.core.provider.InternalController;

@Configuration
@Import({InternalController.class, DependenciesController.class, ApplicationProperties.class})
public class ApplicationCoreConfig {
}