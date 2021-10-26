package no.nav.dolly.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Profile("!test")
@Configuration
@RequiredArgsConstructor
class VaultDatabaseTokenConfig implements InitializingBean {

    private final SecretLeaseContainer container;
    private final HikariDataSource hikariDataSource;
    private final VaultDatabaseProperties properties;

    @Override
    public void afterPropertiesSet() {
        var secret = RequestedSecret.rotating(properties.getBackend() + "/creds/" + properties.getRole());

        container.addLeaseListener(leaseEvent -> {
            log.info("Vault: Lease Event: {}", leaseEvent);
            if (leaseEvent.getSource() == secret && leaseEvent instanceof SecretLeaseCreatedEvent lease) {
                log.info("Vault: Refreshing database credentials. Lease Event: {}", leaseEvent);
                var username = lease.getSecrets().get("username").toString();
                var password = lease.getSecrets().get("password").toString();

                hikariDataSource.setUsername(username);
                hikariDataSource.setPassword(password);
                if (nonNull(hikariDataSource.getHikariPoolMXBean())) {
                    hikariDataSource.getHikariPoolMXBean().softEvictConnections();
                } else {
                    log.info("hikariDataSource.getHikariPoolMXBean() == null -> softEvictConnections ikke utført");
                }
            }
        });

        container.addRequestedSecret(secret);
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfig() {
        return configuration -> configuration
                .initSql(format("SET ROLE \"%s\"", properties.getRole()));
    }
}