package no.nav.testnav.libs.servletcore.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class InternalControllerTest {

    @Test
    @DisplayName("Test response body with null NAIS_APP_IMAGE")
    void testNullNaisAppImage() {

        var env = Mockito.mock(Environment.class);
        when(env.getProperty("NAIS_APP_IMAGE")).thenReturn(null);
        var expectedResponse = new InternalController.JsonResponse("OK", null, null);

        var controller = new InternalController(env);
        assertThat(controller.isAlive())
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(expectedResponse);
                });
        assertThat(controller.isReady())
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(expectedResponse);
                });

    }

    @Test
    @DisplayName("Test response body with expected NAIS_APP_IMAGE")
    void testNonNullNaisAppImage() {

        var env = Mockito.mock(Environment.class);
        when(env.getProperty("NAIS_APP_IMAGE")).thenReturn("europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348");
        var expectedResponse = new InternalController.JsonResponse("OK", "europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348", "https://github.com/navikt/testnorge/commit/36aa348");

        var controller = new InternalController(env);
        assertThat(controller.isAlive())
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(expectedResponse);
                });
        assertThat(controller.isReady())
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(expectedResponse);
                });

    }

}
