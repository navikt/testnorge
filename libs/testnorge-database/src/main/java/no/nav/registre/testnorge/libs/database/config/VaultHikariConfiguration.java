package no.nav.registre.testnorge.libs.database.config;

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
@ConditionalOnProperty(value = "spring.cloud.vault.database.enabled")
public class VaultHikariConfiguration implements InitializingBean {

    private final SecretLeaseContainer container;
    private final HikariDataSource hikariDataSource;
    private final VaultDatabaseProperties props;

    public VaultHikariConfiguration(SecretLeaseContainer container, HikariDataSource hikariDataSource,
                                    VaultDatabaseProperties props) {
        this.container = container;
        this.hikariDataSource = hikariDataSource;
        this.props = props;
    }

    @Override
    public void afterPropertiesSet() {
        container.setLeaseEndpoints(LeaseEndpoints.SysLeases);
        RequestedSecret secret = RequestedSecret.rotating(props.getBackend() + "/creds/" + props.getRole());
        container.addLeaseListener(leaseEvent -> {
            if ((leaseEvent.getSource() == secret) && (leaseEvent instanceof SecretLeaseCreatedEvent)) {
                log.info("Roterer brukernavn/passord for : {}", leaseEvent.getSource().getPath());
                Map<String, Object> secrets = ((SecretLeaseCreatedEvent) leaseEvent).getSecrets();
                String username = secrets.get("username").toString();
                String password = secrets.get("password").toString();
                hikariDataSource.setUsername(username);
                hikariDataSource.setPassword(password);
            }
        });
        container.addRequestedSecret(secret);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [container=" + container + ", hikariDataSource=" + hikariDataSource + ", props=" + props + "]";
    }
}