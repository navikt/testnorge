package no.nav.udistub.config;

import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;

@Configuration
@Import({ApplicationCoreConfig.class,
        FlywayConfiguration.class,
        VaultHikariConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {

}