package no.nav.testnav.apps.oppsummeringsdokumentservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Slf4j
@Configuration
@Import({ApplicationCoreConfig.class})
@RequiredArgsConstructor
public class ApplicationConfig {
}
