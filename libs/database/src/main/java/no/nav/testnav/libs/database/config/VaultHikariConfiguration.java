package no.nav.testnav.libs.database.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.LeaseEndpoints;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnProperty("spring.cloud.vault.database.enabled")
public class VaultHikariConfiguration implements InitializingBean {

    private final SecretLeaseContainer container;
    private final HikariDataSource hikariDataSource;
    private final VaultDatabaseProperties props;

    public VaultHikariConfiguration(SecretLeaseContainer container,
                                    HikariDataSource hikariDataSource,
                                    VaultDatabaseProperties props) {
        this.container = container;
        this.hikariDataSource = hikariDataSource;
        this.props = props;
    }

    @Override
    public void afterPropertiesSet() {
        RequestedSecret secret = RequestedSecret.rotating(props.getBackend() + "/creds/" + props.getRole());
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
                hikariDataSource.getHikariPoolMXBean().softEvictConnections();
            }
        });
        container.addRequestedSecret(secret);
    }

}