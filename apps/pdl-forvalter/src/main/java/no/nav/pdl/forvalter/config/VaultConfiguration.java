package no.nav.pdl.forvalter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

@Slf4j
@Profile("local")
@Configuration
public class VaultConfiguration implements InitializingBean {

    private final SecretLeaseContainer container;
    private final String role;
    private final String backend;

    public VaultConfiguration(
            SecretLeaseContainer container,
            @Value("${spring.cloud.vault.database.role}") String role,
            @Value("${spring.cloud.vault.database.backend}") String backend) {
        this.role = role;
        this.backend = backend;
        this.container = container;
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

                System.setProperty("spring.r2dbc.username", username);
                System.setProperty("spring.r2dbc.password", password);
            }
        });
        container.addRequestedSecret(secret);
    }

}