package no.nav.dolly.libs.vault.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

@AutoConfiguration
@ConditionalOnProperty("spring.cloud.vault.database.enabled")
@RequiredArgsConstructor
@EnableConfigurationProperties({
        DataSourceProperties.class,
        VaultDatabaseProperties.class
})
@Slf4j
public class VaultDatabaseAutoConfiguration implements InitializingBean {

    private final DataSourceProperties dataSourceProperties;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final HikariDataSource dataSource;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final SecretLeaseContainer container;

    private final VaultDatabaseProperties vaultDatabaseProperties;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final VaultTemplate vault;

    /**
     * Setup a rotating lease for the database credentials in Vault.
     * Not configurable as a bean.
     */
    @Override
    public void afterPropertiesSet() {

        var secret = RequestedSecret.rotating(vaultDatabaseProperties.getBackend() + "/creds/" + vaultDatabaseProperties.getRole());
        log.info("Setup vault lease for {}", secret);
        container
                .addLeaseListener(
                        event -> {
                            log.info("Triggering on event: {}", event);
                            if (event.getSource() == secret && event instanceof SecretLeaseCreatedEvent lease) {
                                log.info("Rotating username/password on event: {}", event);
                                var username = lease.getSecrets().get("username").toString();
                                var password = lease.getSecrets().get("password").toString();
                                dataSource.setUsername(username);
                                dataSource.setPassword(password);
                                if (dataSource.getHikariPoolMXBean() != null) {
                                    dataSource.getHikariPoolMXBean().softEvictConnections();
                                }

                            }
                        });
        container.addRequestedSecret(secret);

    }

    @Bean
    FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return new VaultFlywayConfigurationCustomizer(vault, dataSourceProperties, vaultDatabaseProperties);
    }

}
