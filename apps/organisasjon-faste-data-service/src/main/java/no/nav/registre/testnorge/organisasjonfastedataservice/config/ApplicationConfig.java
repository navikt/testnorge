package no.nav.registre.testnorge.organisasjonfastedataservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@Import({ApplicationCoreConfig.class})
@EnableJpaAuditing
public class ApplicationConfig {
}
