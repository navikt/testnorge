package no.nav.dolly.libs.nais;

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
                        case "local", "localdb" -> configureForLocalProfile(environment.getSystemProperties());
                        case "test" -> configureForTestProfile(environment.getSystemProperties());
                        default -> configureForOtherProfiles(environment.getSystemProperties());
                    }
                });

    }

    private static void configureForLocalProfile(Map<String, Object> properties) {

        log.info("Configuring environment for local profile using Secret Manager");

        // Emulating NAIS provided environment variables.
        properties.putIfAbsent("ALTINN_URL", "${sm\\://altinn-url}"); // Used by altinn3-tilgang-service only.
        properties.putIfAbsent("AZURE_APP_CLIENT_ID", "${sm\\://azure-app-client-id}");
        properties.putIfAbsent("AZURE_APP_CLIENT_SECRET", "${sm\\://azure-app-client-secret}");
        properties.putIfAbsent("AZURE_NAV_APP_CLIENT_ID", DUMMY); // Value found in pod, if needed.
        properties.putIfAbsent("AZURE_NAV_APP_CLIENT_SECRET", DUMMY); // Value found in pod, if needed.
        properties.putIfAbsent("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT", "${sm\\://azure-nav-openid-config-token-endpoint}");
        properties.putIfAbsent("AZURE_OPENID_CONFIG_ISSUER", "${sm\\://azure-openid-config-issuer}");
        properties.putIfAbsent("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", "${sm\\://azure-openid-config-token-endpoint}");
        properties.putIfAbsent("CRYPTOGRAPHY_SECRET", DUMMY); // Used by bruker-service only.
        properties.putIfAbsent("JWT_SECRET", DUMMY); // Used by bruker-service only.
        properties.putIfAbsent("MASKINPORTEN_CLIENT_ID", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_CLIENT_JWK", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_SCOPES", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_WELL_KNOWN_URL", "${sm\\://maskinporten-well-known-url}"); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("SLACK_CHANNEL", DUMMY); // Used by tilbakemelding-api only.
        properties.putIfAbsent("SLACK_TOKEN", DUMMY); // Used by tilbakemelding-api only.
        properties.putIfAbsent("TOKEN_X_ISSUER", "${sm\\://token-x-issuer}");

    }

    private static void configureForTestProfile(Map<String, Object> properties) {

        log.info("Configuring environment for test profile using dummy values");

        // Disabling Secret Manager (not available when running builds on GitHub).
        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", "false");

        // Setting dummy placeholders.
        Stream
                .of(
                        "spring.cloud.vault.token", // For apps using no.nav.testnav.libs:vault.

                        "ALTINN_API_KEY",
                        "ALTINN_URL",
                        "AZURE_OPENID_CONFIG_ISSUER",
                        "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
                        "CRYPTOGRAPHY_SECRET", // Used by bruker-service only.
                        "IDPORTEN_CLIENT_ID", // Used by dolly-frontend only.
                        "IDPORTEN_CLIENT_JWK", // Used by dolly-frontend only.
                        "JWT_SECRET", // Used by bruker-service only.
                        "MASKINPORTEN_CLIENT_ID",
                        "MASKINPORTEN_CLIENT_JWK",
                        "MASKINPORTEN_SCOPES",
                        "MASKINPORTEN_WELL_KNOWN_URL",
                        "TOKEN_X_CLIENT_ID",
                        "TOKEN_X_ISSUER"
                )
                .forEach(key -> properties.putIfAbsent(key, DUMMY));

    }

    private static void configureForOtherProfiles(Map<String, Object> properties) {

        log.info("Configuring environment for non-test, non-local profiles");

        properties.putIfAbsent("spring.main.banner-mode", "off");
        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", "false"); // Unless we actually start using Secret Manager in deployment.

    }

}
