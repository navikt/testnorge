package no.nav.dolly.libs.vault.database;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class VaultFlywayConfigurationCustomizer implements FlywayConfigurationCustomizer {

    private final VaultTemplate vault;
    private final VaultDatabaseProperties config;

    @Override
    public void customize(FluentConfiguration configuration) {

        var secretPath = "%s/creds/%s".formatted(config.getBackend(), config.getRole());
        var response = Optional
                .of(vault.read(secretPath))
                .map(VaultResponse::getData)
                .orElseThrow(() -> new IllegalStateException("Could not read credentials from Vault path %s".formatted(secretPath)));
        var username = response.get("username").toString();
        var password = response.get("password").toString();
        configuration
                .dataSource(configuration.getUrl(), username, password)
                .initSql("SET ROLE \"%s\"".formatted(config.getRole()));
        log.info("Flyway configured with credentials from Vault path {}", secretPath);

    }

}
