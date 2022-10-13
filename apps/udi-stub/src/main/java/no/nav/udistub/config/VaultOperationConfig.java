package no.nav.udistub.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("prod")
@Configuration
public class VaultOperationConfig {

    private static final String tokenFile = "/var/run/secrets/nais.io/vault/vault_token";

    @ConditionalOnMissingBean(VaultOperations.class)
    @Bean
    @SneakyThrows
    public VaultOperations vaultOperations(@Value("${spring.cloud.vault.host}") String vaultHost,
                                           @Value("${spring.cloud.vault.port}") Integer vaultPort) {

        String token;
        try (var reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(tokenFile), StandardCharsets.UTF_8))) {
            token = reader.readLine();
            log.info("Vault token: {}", token);
        }
        return new VaultTemplate(VaultEndpoint.create(vaultHost, vaultPort), new TokenAuthentication(token));
    }
}
