package no.nav.dolly.libs.vault.database;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayConfigurationCustomizer;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class VaultFlywayConfigurationCustomizer implements FlywayConfigurationCustomizer {

    private final VaultTemplate vault;
    private final DataSourceProperties dataSourceProperties;
    private final VaultDatabaseProperties vaultDatabaseProperties;

    @Override
    public void customize(FluentConfiguration configuration) {

        var secretPath = "%s/creds/%s".formatted(vaultDatabaseProperties.getBackend(), vaultDatabaseProperties.getRole());
        var response = Optional
                .of(vault.read(secretPath))
                .map(VaultResponse::getData)
                .orElseThrow(() -> new IllegalStateException("Could not read credentials from Vault path %s".formatted(secretPath)));
        var username = response.get("username").toString();
        var password = response.get("password").toString();
        configuration
                .dataSource(dataSourceProperties.getUrl(), username, password)
                .initSql("SET ROLE \"%s\"".formatted(vaultDatabaseProperties.getRole()));
        log.info("Flyway configured with credentials from Vault path {}", secretPath);

    }

}
