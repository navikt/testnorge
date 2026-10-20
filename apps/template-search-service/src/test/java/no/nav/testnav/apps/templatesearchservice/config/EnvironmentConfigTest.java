package no.nav.testnav.apps.templatesearchservice.config;

import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(
                    Consumers.class,
                    AzureAdResourceServerProperties.class,
                    TokenxResourceServerProperties.class,
                    R2dbcSslConfig.class)
            .withPropertyValues(
                    "AZURE_OPENID_CONFIG_ISSUER=https://azure.example.test",
                    "AZURE_APP_CLIENT_ID=azure-client");

    @Test
    void shouldUseDevBackendAndAzureOnlyWithoutTokenxConfiguration() {
        contextRunner.withPropertyValues("spring.profiles.active=dev")
                .run(context -> {
                    assertThat(context).hasNotFailed()
                            .hasSingleBean(AzureAdResourceServerProperties.class)
                            .hasSingleBean(R2dbcSslConfig.class)
                            .doesNotHaveBean(TokenxResourceServerProperties.class);
                    var dollyBackend = context.getBean(Consumers.class).getDollyBackend();
                    assertThat(dollyBackend.getName()).isEqualTo("dolly-backend-dev");
                    assertThat(dollyBackend.getUrl()).isEqualTo("http://dolly-backend-dev.dolly.svc.cluster.local");
                });
    }

    @Test
    void shouldKeepMainBackendAndTokenxInNonDev() {
        contextRunner.withPropertyValues(
                        "spring.profiles.active=prod",
                        "TOKEN_X_ISSUER=https://tokenx.example.test",
                        "TOKEN_X_CLIENT_ID=tokenx-client")
                .run(context -> {
                    assertThat(context).hasNotFailed()
                            .hasSingleBean(AzureAdResourceServerProperties.class)
                            .hasSingleBean(R2dbcSslConfig.class)
                            .hasSingleBean(TokenxResourceServerProperties.class);
                    var dollyBackend = context.getBean(Consumers.class).getDollyBackend();
                    assertThat(dollyBackend.getName()).isEqualTo("dolly-backend");
                    assertThat(dollyBackend.getUrl()).isEqualTo("http://dolly-backend.dolly.svc.cluster.local");
                    assertThat(context.getBean(TokenxResourceServerProperties.class).getIssuerUri())
                            .isEqualTo("https://tokenx.example.test");
                });
    }

    @Test
    void shouldUseSeparateDatabaseVariablesForEachEnvironment() {
        assertDatabase("prod", "NAIS_DATABASE_TESTNAV_TEMPLATE_SEARCH_SERVICE_TESTNAV_TEMPLATE_SEARCH_SERVICE_DB",
                "main-database");
        assertDatabase("dev", "NAIS_DATABASE_TESTNAV_TEMPLATE_SEARCH_SERVICE_DEV_TESTNAV_TEMPLATE_SEARCH_SERVICE_DEV_DB",
                "dev-database");
    }

    @Test
    void shouldUseDevBackendForLocalTemplateService() {
        contextRunner.withPropertyValues("spring.profiles.active=local")
                .run(context -> {
                    assertThat(context).hasNotFailed()
                            .doesNotHaveBean(TokenxResourceServerProperties.class);
                    assertThat(context.getBean(Consumers.class).getDollyBackend().getName())
                            .isEqualTo("dolly-backend-dev");
                });
    }

    private void assertDatabase(String profile, String prefix, String database) {
        contextRunner.withPropertyValues(
                        "spring.profiles.active=" + profile,
                        "TOKEN_X_ISSUER=https://tokenx.example.test",
                        "TOKEN_X_CLIENT_ID=tokenx-client",
                        prefix + "_HOST=database-host",
                        prefix + "_PORT=5432",
                        prefix + "_DATABASE=" + database,
                        prefix + "_USERNAME=database-user",
                        prefix + "_PASSWORD=test-password",
                        prefix + "_JDBC_URL=jdbc:postgresql://database-host:5432/" + database,
                        prefix + "_SSLCERT=/cert.pem",
                        prefix + "_SSLROOTCERT=/root-cert.pem",
                        prefix + "_SSLMODE=verify-ca")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var environment = context.getEnvironment();
                    assertThat(environment.getProperty("spring.r2dbc.url"))
                            .isEqualTo("r2dbc:postgresql://database-host:5432/" + database);
                    assertThat(environment.getProperty("spring.flyway.url"))
                            .isEqualTo("jdbc:postgresql://database-host:5432/" + database);
                    assertThat(environment.getProperty("spring.r2dbc.properties.sslMode")).isEqualTo("verify-ca");
                    assertThat(environment.getProperty("spring.r2dbc.properties.sslCert")).isEqualTo("/cert.pem");
                    assertThat(environment.getProperty("spring.r2dbc.properties.sslRootCert")).isEqualTo("/root-cert.pem");
                });
    }
}
