package no.nav.testnav.libs.servletcore.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class InternalControllerTest {

    private InternalController internalController;

    @Test
    @DisplayName("Test response body with null NAIS_APP_IMAGE")
    void testNullNaisAppImage() {

        internalController = new InternalController(null);

        assertThat(internalController.isAlive())
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                });
        assertThat(internalController.isReady())
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                });
        assertThat(internalController.getVersion())
                .isNotNull()
                .satisfies(response -> {
                    assertNull(response.commit());
                    assertNull(response.image());
                });
    }

    @Test
    @DisplayName("Test response body with expected NAIS_APP_IMAGE")
    void testNonNullNaisAppImage() {

        var internalController = new InternalController("europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348");

        assertThat(internalController.getVersion())
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.commit()).isEqualTo("https://github.com/navikt/testnorge/commit/36aa348");
                    assertThat(response.image()).isEqualTo("europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348");
                });
    }
}
