package no.nav.dolly.libs.nais;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

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
                        case "local", "localdb" -> configureForLocalProfile(environment);
                        case "test" -> configureForTestProfile(environment);
                        default -> configureForOtherProfiles(environment);
                    }
                });

    }

    private static void configureForLocalProfile(ConfigurableEnvironment environment) {

        log.info("Configuring environment for local profile using Secret Manager");
        var properties = environment.getSystemProperties();

        // Dynamically load any configured environment variables from NAIS pod.
        try {
            new NaisRuntimeEnvironmentConnector(environment)
                    .getEnvironmentVariables()
                    .forEach(properties::putIfAbsent);
        } catch (NaisEnvironmentException e) {
            log.warn("Failed to dynamically load environment variables using {}", NaisRuntimeEnvironmentConnector.class.getSimpleName(), e);
        }

        // Enable all actuator endpoints.
        properties.putIfAbsent("management.endpoints.web.exposure.include", "*");

        // Emulating NAIS provided environment variables.
        properties.putIfAbsent("AZURE_APP_CLIENT_ID", "${sm\\://azure-app-client-id}");
        properties.putIfAbsent("AZURE_APP_CLIENT_SECRET", "${sm\\://azure-app-client-secret}");
        properties.putIfAbsent("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT", "${sm\\://azure-nav-openid-config-token-endpoint}"); // Corresponding AZURE_NAV_APP_CLIENT_[ID|SECRET] can be loaded from pod, if needed.
        properties.putIfAbsent("AZURE_OPENID_CONFIG_ISSUER", "${sm\\://azure-openid-config-issuer}");
        properties.putIfAbsent("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", "${sm\\://azure-openid-config-token-endpoint}");
        properties.putIfAbsent("AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT", "${sm\\://azure-trygdeetaten-openid-config-token-endpoint}"); // Corresponding AZURE_TRYGDEETATEN_APP_CLIENT_[ID|SECRET] can be loaded from pod, if needed.
        properties.putIfAbsent("JWT_SECRET", DUMMY);
        properties.putIfAbsent("MASKINPORTEN_CLIENT_ID", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_CLIENT_JWK", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_SCOPES", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_WELL_KNOWN_URL", "${sm\\://maskinporten-well-known-url}"); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("TOKEN_X_ISSUER", "${sm\\://token-x-issuer}");

    }

    private static void configureForTestProfile(ConfigurableEnvironment environment) {

        log.info("Configuring environment for test profile using dummy values");
        var properties = environment.getSystemProperties();

        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", "false"); // Disabling Secret Manager (not available when running builds on GitHub).
        properties.putIfAbsent("spring.main.banner-mode", "off"); // Disabling Spring Boot banner.
        properties.putIfAbsent("dolly.texas.preload", "false"); // Don't preload Texas tokens in test profile.

        // These will be set to value "dummy".
        Stream
                .of(
                        "spring.cloud.vault.token", // For apps using no.nav.testnav.libs:vault.

                        // For apps using Texas.
                        "dolly.texas.url.exchange",
                        "dolly.texas.url.introspect",
                        "dolly.texas.url.token",

                        "ALTINN_API_KEY",
                        "AZURE_OPENID_CONFIG_ISSUER",
                        "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
                        "JWT_SECRET",
                        "MASKINPORTEN_CLIENT_ID",
                        "MASKINPORTEN_CLIENT_JWK",
                        "MASKINPORTEN_SCOPES",
                        "MASKINPORTEN_WELL_KNOWN_URL",
                        "TOKEN_X_CLIENT_ID",
                        "TOKEN_X_ISSUER"
                )
                .forEach(key -> properties.putIfAbsent(key, DUMMY));

    }

    private static void configureForOtherProfiles(ConfigurableEnvironment environment) {

        log.info("Configuring environment for non-test, non-local profiles");
        var properties = environment.getSystemProperties();

        properties.putIfAbsent("spring.main.banner-mode", "off");
        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", "false"); // Unless we actually start using Secret Manager in deployment.

    }

}
