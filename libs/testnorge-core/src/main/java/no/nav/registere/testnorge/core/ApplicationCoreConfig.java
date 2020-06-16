package no.nav.registere.testnorge.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registere.testnorge.core.provider.InternalController;
import no.nav.registre.testnorge.dependencyanalysis.provider.DependenciesController;

@Configuration
@Import({InternalController.class, DependenciesController.class})
public class ApplicationCoreConfig {
}