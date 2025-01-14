package no.nav.registre.testnorge.organisasjonfastedataservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Configuration
@Import({ApplicationCoreConfig.class})
@EnableJpaAuditing
public class ApplicationConfig {
}
