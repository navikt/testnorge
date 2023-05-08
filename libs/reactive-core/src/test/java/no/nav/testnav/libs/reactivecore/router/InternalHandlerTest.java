package no.nav.testnav.libs.reactivecore.router;

import io.micrometer.common.lang.NonNullApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@NonNullApi
class InternalHandlerTest {

    private static final ServerResponse.Context CONTEXT = new ServerResponse.Context() {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return HandlerStrategies.withDefaults().messageWriters();
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return Collections.emptyList();
        }
    };

    @Test
    @DisplayName("Test response body with null NAIS_APP_IMAGE")
    void testNullNaisAppImage() {

        var env = Mockito.mock(Environment.class);
        when(env.getProperty("NAIS_APP_IMAGE")).thenReturn(null);

        var controller = new InternalHandler(env);
        assertThat(controller.isAlive(null))
                .isNotNull()
                .satisfies(mono -> assertThat(mono.block())
                        .isNotNull()
                        .satisfies(response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/internal/isAlive"));
                            response.writeTo(exchange, CONTEXT).block();
                            assertThat(exchange.getResponse().getBodyAsString().block())
                                    .isNotNull()
                                    .satisfies(html -> assertThat(html).isEqualTo("OK"));
                        }));

        assertThat(controller.isReady(null))
                .isNotNull()
                .satisfies(mono -> assertThat(mono.block())
                        .isNotNull()
                        .satisfies(response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/internal/isReady"));
                            response.writeTo(exchange, CONTEXT).block();
                            assertThat(exchange.getResponse().getBodyAsString().block())
                                    .isNotNull()
                                    .satisfies(html -> assertThat(html).isEqualTo("OK"));
                        }));

    }

    @Test
    @DisplayName("Test response body with expected NAIS_APP_IMAGE")
    void testNonNullNaisAppImage() {

        var env = Mockito.mock(Environment.class);
        when(env.getProperty("NAIS_APP_IMAGE")).thenReturn("europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348");

        var controller = new InternalHandler(env);
        assertThat(controller.isAlive(null))
                .isNotNull()
                .satisfies(mono -> assertThat(mono.block())
                        .isNotNull()
                        .satisfies(response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/internal/isAlive"));
                            response.writeTo(exchange, CONTEXT).block();
                            assertThat(exchange.getResponse().getBodyAsString().block())
                                    .isNotNull()
                                    .satisfies(html -> assertThat(html).isEqualTo("OK - image is <a href=https://github.com/navikt/testnorge/commit/36aa348>europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348</a>"));
                        }));

        assertThat(controller.isReady(null))
                .isNotNull()
                .satisfies(mono -> assertThat(mono.block())
                        .isNotNull()
                        .satisfies(response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/internal/isAlive"));
                            response.writeTo(exchange, CONTEXT).block();
                            assertThat(exchange.getResponse().getBodyAsString().block())
                                    .isNotNull()
                                    .satisfies(html -> assertThat(html).isEqualTo("OK - image is <a href=https://github.com/navikt/testnorge/commit/36aa348>europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348</a>"));
                        }));

    }

}
