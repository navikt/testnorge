package no.nav.registre.testnorge.oppsummeringsdokumentservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Slf4j
@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@RequiredArgsConstructor
public class ApplicationConfig {
}
