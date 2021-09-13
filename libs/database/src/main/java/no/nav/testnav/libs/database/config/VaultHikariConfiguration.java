package no.nav.testnav.libs.database.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

@Slf4j
@Configuration
@ConditionalOnProperty("spring.cloud.vault.database.enabled")
public class VaultHikariConfiguration implements InitializingBean {

    private final SecretLeaseContainer container;
    private final HikariDataSource hikariDataSource;
    private final String role;
    private final String backend;

    public VaultHikariConfiguration(
            SecretLeaseContainer container,
            @Value("${spring.cloud.vault.database.role}") String role,
            @Value("${spring.cloud.vault.database.backend}") String backend,
            HikariDataSource hikariDataSource) {
        this.role = role;
        this.backend = backend;
        this.container = container;
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void afterPropertiesSet() {
        RequestedSecret secret = RequestedSecret.rotating(backend + "/creds/" + role);
        log.info("Setup vault lease for {}", secret);

        container.addLeaseListener(leaseEvent -> {
            log.info("Vault: Lease Event: {}", leaseEvent);
            if (leaseEvent.getSource() == secret && leaseEvent instanceof SecretLeaseCreatedEvent) {
                log.info("Roterer brukernavn/passord for: {}", leaseEvent);

                var lease = (SecretLeaseCreatedEvent) leaseEvent;

                var username = lease.getSecrets().get("username").toString();
                var password = lease.getSecrets().get("password").toString();
                hikariDataSource.setUsername(username);
                hikariDataSource.setPassword(password);
                if(hikariDataSource.getHikariPoolMXBean() != null){
                    hikariDataSource.getHikariPoolMXBean().softEvictConnections();
                }

            }
        });
        container.addRequestedSecret(secret);
    }

}