package no.nav.registre.testnav.statistikkservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.registre.testnorge.libs.core.config.AnalysisGCPAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;

@EnableJpaAuditing
@Configuration
@Import({
        ApplicationCoreConfig.class,
        AnalysisGCPAutoConfiguration.class
})
public class ApplicationConfig {

}
