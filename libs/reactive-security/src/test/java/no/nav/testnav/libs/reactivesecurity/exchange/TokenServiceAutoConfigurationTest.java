package no.nav.testnav.libs.reactivesecurity.exchange;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTokenService;
import no.nav.testnav.libs.securitycore.texas.TexasClientAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TokenServiceAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    PropertyPlaceholderAutoConfiguration.class,
                    TexasClientAutoConfiguration.class,
                    TokenServiceAutoConfiguration.class))
            .withBean(WebClient.class, WebClient::create)
            .withBean(GetAuthenticatedToken.class, () -> mock(GetAuthenticatedToken.class))
            .withBean(GetAuthenticatedUserId.class, () -> mock(GetAuthenticatedUserId.class))
            .withBean(ObjectMapper.class, () -> mock(ObjectMapper.class));

    @Test
    void shouldUseTexasForEntraId() {
        contextRunner
                .withPropertyValues(
                        "NAIS_TOKEN_ENDPOINT=http://127.0.0.1:7164/api/v1/token",
                        "NAIS_TOKEN_EXCHANGE_ENDPOINT=http://127.0.0.1:7164/api/v1/token/exchange",
                        "NAIS_TOKEN_INTROSPECTION_ENDPOINT=http://127.0.0.1:7164/api/v1/introspect")
                .run(context -> assertThat(context).hasSingleBean(AzureTokenService.class));
    }

    @Test
    void shouldFailWithoutTexas() {
        contextRunner
                .run(context -> assertThat(context).hasFailed());
    }
}
