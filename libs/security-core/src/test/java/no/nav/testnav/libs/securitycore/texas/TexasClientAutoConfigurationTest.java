package no.nav.testnav.libs.securitycore.texas;

import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredentialAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class TexasClientAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    PropertyPlaceholderAutoConfiguration.class,
                    ClientCredentialAutoConfiguration.class,
                    TexasClientAutoConfiguration.class));

    @Test
    void shouldUseTexasFromNais() {
        contextRunner
                .withPropertyValues(
                        "NAIS_TOKEN_ENDPOINT=http://127.0.0.1:7164/api/v1/token",
                        "NAIS_TOKEN_EXCHANGE_ENDPOINT=http://127.0.0.1:7164/api/v1/token/exchange",
                        "NAIS_TOKEN_INTROSPECTION_ENDPOINT=http://127.0.0.1:7164/api/v1/introspect")
                .run(context -> assertThat(context.getBean(TexasClient.class)).isExactlyInstanceOf(TexasClient.class));
    }

    @Test
    void shouldUseLocalauthForEntraIdWhenRunningLocally() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=local",
                        "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT=https://dolly-auth-local.intern.dev.nav.no/entraid/oauth2/token",
                        "AZURE_APP_CLIENT_ID=local-client",
                        "AZURE_APP_CLIENT_SECRET=dummy",
                        "AZURE_OPENID_CONFIG_ISSUER=https://login.microsoftonline.com/tenant/v2.0",
                        "TOKEN_X_ISSUER=https://tokenx.dev-gcp.nav.cloud.nais.io",
                        "dolly.texas.url.token=https://dolly-texas-proxy.intern.dev.nav.no/api/v1/token",
                        "dolly.texas.url.exchange=https://dolly-texas-proxy.intern.dev.nav.no/api/v1/token/exchange")
                .run(context -> assertThat(context.getBean(TexasClient.class)).isInstanceOf(LocalTexasClient.class));
    }

    @Test
    void shouldNotCreateTexasClientWithoutEndpoint() {
        contextRunner
                .run(context -> assertThat(context).doesNotHaveBean(TexasClient.class));
    }
}
