package no.nav.testnav.proxies.skjermingsregisterproxy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LocalVaultConfigTest {

    @Test
    void missingSystemPropertyShouldFail() {
        assertThatThrownBy(new LocalVaultConfig()::clientAuthentication)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void environmentPropertyShouldTakePrecedenceOverSystemProperty() {
        try {
            System.setProperty(LocalVaultConfig.TOKEN_PROPERTY_NAME, "<some-token-here>");
            assertThat(new LocalVaultConfig())
                    .isNotNull();
        } finally {
            System.clearProperty(LocalVaultConfig.TOKEN_PROPERTY_NAME);
        }
    }

    @Test
    void vaultEndpointShouldBeKnown() {
        assertThat(new LocalVaultConfig().vaultEndpoint())
                .isNotNull()
                .satisfies(endpoint -> {
                    assertThat(endpoint.getHost()).isEqualTo("vault.adeo.no");
                    assertThat(endpoint.getPort()).isEqualTo(443);
                });
    }

}
