package no.nav.registre.aaregstub.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

@Slf4j
@Configuration
@ConditionalOnClass(SecretLeaseContainer.class)
public class VaultConfig implements InitializingBean {

    @Autowired
    private SecretLeaseContainer container;

    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private VaultDatabaseProperties properties;

    @Override
    public void afterPropertiesSet() {
        val secret = RequestedSecret.rotating(properties.getBackend() + "/creds/" + properties.getRole());

        container.addLeaseListener(leaseEvent -> {
            log.info("Vault: Lease Event: {}", leaseEvent);
            if (leaseEvent.getSource() == secret && leaseEvent instanceof SecretLeaseCreatedEvent) {
                log.info("Vault: Refreshing database credentials. Lease Event: {}", leaseEvent);
                val slce = (SecretLeaseCreatedEvent) leaseEvent;
                val username = slce.getSecrets().get("username").toString();
                val password = slce.getSecrets().get("password").toString();

                hikariDataSource.setUsername(username);
                hikariDataSource.setPassword(password);
                hikariDataSource.getHikariConfigMXBean().setUsername(username);
                hikariDataSource.getHikariConfigMXBean().setPassword(password);
            }
        });

        container.addRequestedSecret(secret);
    }
}
