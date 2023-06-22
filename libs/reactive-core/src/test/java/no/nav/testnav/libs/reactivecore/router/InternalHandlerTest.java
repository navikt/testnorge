package no.nav.testnav.libs.reactivecore.router;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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

        var internalHandler = new InternalHandler(null);

        StepVerifier
                .create(internalHandler.isAlive(null))
                .expectNextMatches(status -> status.statusCode() == HttpStatus.OK)
                .verifyComplete();

        StepVerifier
                .create(internalHandler.isReady(null))
                .expectNextMatches(status -> status.statusCode() == HttpStatus.OK)
                .verifyComplete();

        StepVerifier
                .create(internalHandler.getVersion(null))
                .expectNextMatches(status -> status.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test response body with expected NAIS_APP_IMAGE")
    void testNonNullNaisAppImage() {

        var handler = new InternalHandler("europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348");

        assertThat(handler.getVersion(null))
                .isNotNull()
                .satisfies(mono -> assertThat(mono.block())
                        .isNotNull()
                        .satisfies(response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/internal/isAlive"));
                            response.writeTo(exchange, CONTEXT).block();
                            assertThat(exchange.getResponse().getBodyAsString().block())
                                    .isNotNull()
                                    .satisfies(body -> assertThat(body).isEqualTo("{\"image\":\"europe-north1-docker.pkg.dev/nais-management-233d/dolly/testnorge-dolly-backend:2023.05.04-13.27-36aa348\",\"commit\":\"https://github.com/navikt/testnorge/commit/36aa348\"}"));
                        }));
    }
}
