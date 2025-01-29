package no.nav.testnav.libs.nais;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class NaisEnvironmentApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String DUMMY = "dummy";

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context) {

        var environment = context.getEnvironment();
        Stream
                .of(environment.getActiveProfiles())
                .forEach(profile -> {
                    switch (profile) {
                        case "local" -> configureForLocalProfile(environment.getSystemProperties());
                        case "test" -> configureForTestProfile(environment.getSystemProperties());
                        default -> { /* Do nothing. */ }
                    }
                });

    }

    private static void configureForLocalProfile(Map<String, Object> properties) {

        log.info("Configuring environment for local profile using Secret Manager");

        // Emulating NAIS provided environment variables.
        properties.putIfAbsent("AZURE_APP_CLIENT_ID", "${sm\\://azure-app-client-id}");
        properties.putIfAbsent("AZURE_APP_CLIENT_SECRET", "${sm\\://azure-app-client-secret}");
        properties.putIfAbsent("AZURE_OPENID_CONFIG_ISSUER", "${sm\\://azure-openid-config-issuer}");
        properties.putIfAbsent("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", "${sm\\://azure-openid-config-token-endpoint}");
        properties.putIfAbsent("TOKEN_X_ISSUER", "${sm\\://token-x-issuer}");
        properties.putIfAbsent("TOKEN_X_JWKS_URI", "${sm\\://token-x-jwks-uri}");

    }

    private static void configureForTestProfile(Map<String, Object> properties) {

        log.info("Configuring environment for test profile using dummy values");

        // Disabling Secret Manager (not available when running builds on GitHub).
        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", "false");

        // Setting dummy placeholders.
        Stream
                .of(
                        "ALTINN_API_KEY",
                        "ALTINN_URL",
                        "AZURE_OPENID_CONFIG_ISSUER",
                        "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", // CATO: Remove?
                        "CRYPTOGRAPHY_SECRET", // Used by bruker-service only.
                        "IDPORTEN_CLIENT_ID", // Used by dolly-frontend only.
                        "IDPORTEN_CLIENT_JWK", // Used by dolly-frontend only.
                        "JWT_SECRET", // Used by bruker-service only.
                        "MASKINPORTEN_CLIENT_ID",
                        "MASKINPORTEN_CLIENT_JWK",
                        "MASKINPORTEN_SCOPES",
                        "MASKINPORTEN_WELL_KNOWN_URL",
                        "TOKEN_X_CLIENT_ID",
                        "TOKEN_X_ISSUER",
                        "spring.cloud.vault.token" // For apps using no.nav.testnav.libs:vault.
                )
                .forEach(key -> properties.putIfAbsent(key, DUMMY));

    }

}
