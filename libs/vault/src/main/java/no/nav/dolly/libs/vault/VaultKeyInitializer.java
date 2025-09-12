package no.nav.dolly.libs.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class VaultKeyInitializer implements EnvironmentPostProcessor {

    private static final String KEY = "SPRING_CLOUD_VAULT_TOKEN";
    private static final String PATH = "/var/run/secrets/nais.io/vault/vault_token";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {

            try {
                var path = Paths.get(PATH);
                if (Files.exists(path)) {
                    var value = Files.readString(path).trim();
                    System.setProperty(KEY, value); // System property.
                    environment.getPropertySources().addFirst( // Spring property.
                            new MapPropertySource("spring.cloud.vault.token", Map.of(KEY, value))
                    );
                    System.out.printf("System property %s set from file %s%n", KEY, PATH);
                } else {
                    throw new FileNotFoundException("File not found at %s; cannot run without Vault token".formatted(PATH));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error setting system property %s from file %s".formatted(KEY, PATH), e);
            }

        } else {
            System.out.printf("Not in profile 'prod'; not setting Vault token from file %s%n", PATH);
        }

    }
}
