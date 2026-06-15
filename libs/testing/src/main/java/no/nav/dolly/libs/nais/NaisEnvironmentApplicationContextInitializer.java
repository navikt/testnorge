package no.nav.dolly.libs.nais;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.stream.Stream;

@Slf4j
public class NaisEnvironmentApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    // Konfigurasjon for lokal kjoering er hentet herfra: https://github.com/navikt/localauth
    private static final String APP_CLIENT_ID = "669db109-2dbb-4c4c-93cd-cbc6aa2ef1a4";
    private static final String AUDIENCE = "3cbdd4cb-d048-420f-889e-2b32b7add652";
    private static final String LOCAL_AUTH = "https://dolly-auth-local.intern.dev.nav.no";
    private static final String PROVIDER_URL = "https://login.microsoftonline.com";
    private static final String TENENT_ID = "nav.no";
    private static final String DUMMY = "dummy";
    private static final String FALSE = "false";

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

        // Enable all actuator endpoints.
        properties.putIfAbsent("management.endpoints.web.exposure.include", "*");

        // Point Texas to dolly-texas-proxy.
        properties.putIfAbsent("dolly.texas.url.token", "https://dolly-texas-proxy.intern.dev.nav.no/api/v1/token");
        properties.putIfAbsent("dolly.texas.url.exchange", "https://dolly-texas-proxy.intern.dev.nav.no/api/v1/token/exchange");
        properties.putIfAbsent("dolly.texas.url.introspect", "https://dolly-texas-proxy.intern.dev.nav.no/api/v1/introspect");

        properties.putIfAbsent("spring.security.oauth2.resourceserver.aad.issuer-uri",
                PROVIDER_URL + "/62366534-1ec3-4962-8869-9b5535279d0b/v2.0");
        properties.putIfAbsent("spring.security.oauth2.resourceserver.aad.accepted-audience",
                "%s, api:// %s".formatted(AUDIENCE, AUDIENCE));

        // Emulating NAIS provided environment variables.
        properties.putIfAbsent("AZURE_APP_CLIENT_ID", APP_CLIENT_ID);
        properties.putIfAbsent("AZURE_APP_CLIENT_SECRET", DUMMY);
        properties.putIfAbsent("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT", LOCAL_AUTH + "/entraid/oauth2/token");
        properties.putIfAbsent("AZURE_OPENID_CONFIG_ISSUER", "%s/%s/v2.0".formatted(PROVIDER_URL, TENENT_ID));
        properties.putIfAbsent("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", LOCAL_AUTH + "/entraid/oauth2/token");
        properties.putIfAbsent("JWT_SECRET", DUMMY);
        properties.putIfAbsent("MASKINPORTEN_CLIENT_ID", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_CLIENT_JWK", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_SCOPES", DUMMY); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("MASKINPORTEN_WELL_KNOWN_URL", "https://test.maskinporten.no/.well-known/oauth-authorization-server"); // Used by tenor-search-service and altinn3-tilgang-service only.
        properties.putIfAbsent("TOKEN_X_ISSUER", "https://tokenx.dev-gcp.nav.cloud.nais.io");

    }

    private static void configureForTestProfile(ConfigurableEnvironment environment) {

        log.info("Configuring environment for test profile using dummy values");
        var properties = environment.getSystemProperties();

        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", FALSE); // Disabling Secret Manager (not available when running builds on GitHub).
        properties.putIfAbsent("dolly.texas.preload", FALSE); // Don't preload Texas tokens in test profile.

        // These will be set to value "dummy".
        Stream.of(

                        // For apps using no.nav.testnav.libs:vault.
                        "spring.cloud.vault.token",

                        "ALTINN_API_KEY",

                        "AZURE_APP_CLIENT_ID",
                        "AZURE_APP_CLIENT_SECRET",

                        "JWT_SECRET",
                        "MASKINPORTEN_CLIENT_ID",
                        "MASKINPORTEN_CLIENT_JWK",
                        "MASKINPORTEN_SCOPES",

                        "TOKEN_X_CLIENT_ID"
                )
                .forEach(key -> properties.putIfAbsent(key, DUMMY));

        // These will be set to value "http://dummy".
        Stream.of(

                        // For apps using Texas.
                        "dolly.texas.url.exchange",
                        "dolly.texas.url.introspect",
                        "dolly.texas.url.token",

                        "AZURE_OPENID_CONFIG_ISSUER",
                        "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
                        "MASKINPORTEN_WELL_KNOWN_URL",
                        "TOKEN_X_ISSUER"

                )
                .forEach(key -> properties.putIfAbsent(key, "http://" + DUMMY));


    }

    private static void configureForOtherProfiles(ConfigurableEnvironment environment) {

        log.info("Configuring environment for non-test, non-local profiles");
        var properties = environment.getSystemProperties();

        properties.putIfAbsent("spring.cloud.gcp.secretmanager.enabled", FALSE); // Unless we actually start using Secret Manager in deployment.

    }

}
