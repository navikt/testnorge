package no.nav.testnav.proxies.skjermingsregisterproxy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DevVaultConfigTest {

    @Test
    void missingSystemPropertyShouldFail() {
        assertThatThrownBy(new DevVaultConfig()::clientAuthentication)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void environmentPropertyShouldTakePrecedenceOverSystemProperty() {
        try {
            System.setProperty(DevVaultConfig.TOKEN_PROPERTY_NAME, "<some-token-here>");
            assertThat(new DevVaultConfig())
                    .isNotNull();
        } finally {
            System.clearProperty(DevVaultConfig.TOKEN_PROPERTY_NAME);
        }
    }

    @Test
    void vaultEndpointShouldBeKnown() {
        assertThat(new DevVaultConfig().vaultEndpoint())
                .isNotNull()
                .satisfies(endpoint -> {
                    assertThat(endpoint.getHost()).isEqualTo("vault.adeo.no");
                    assertThat(endpoint.getPort()).isEqualTo(443);
                });
    }

}
