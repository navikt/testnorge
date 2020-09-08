package no.nav.freg.token.provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registere.testnorge.core.config.ApplicationCoreConfig;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {
}
