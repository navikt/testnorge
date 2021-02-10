package no.nav.udistub.config;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Import;

@Import({ApplicationCoreConfig.class,
        FlywayAutoConfiguration.class,
        VaultHikariConfiguration.class})
public class AppConfig {

}
