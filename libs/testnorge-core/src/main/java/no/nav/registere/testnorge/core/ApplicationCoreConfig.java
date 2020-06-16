package no.nav.registere.testnorge.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registere.testnorge.core.provider.InternalController;

@Configuration
@Import(InternalController.class)
public class ApplicationCoreConfig {
}