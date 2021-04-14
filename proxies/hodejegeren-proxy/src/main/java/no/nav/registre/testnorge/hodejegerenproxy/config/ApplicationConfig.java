package no.nav.registre.testnorge.hodejegerenproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import(ApplicationCoreConfig.class)
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {
}
