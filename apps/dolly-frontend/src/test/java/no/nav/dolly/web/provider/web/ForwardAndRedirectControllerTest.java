package no.nav.dolly.web.provider.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ForwardAndRedirectControllerTest {

    private final ForwardAndRedirectController controller = new ForwardAndRedirectController();
    private final ByteArrayResource html = new ByteArrayResource(
            "<html><head><title>Dolly</title></head><body><div id=\"root\"></div></body></html>"
                    .getBytes(StandardCharsets.UTF_8));

    @ParameterizedTest
    @ValueSource(strings = {"dolly-frontend", "dolly-frontend-dev", "dolly-idporten"})
    void shouldRenderDeploymentMetadataOnEveryFrontendRoute(String appName) {
        var router = controller.htmlRouter(html, true, appName, "dolly", "dev-gcp",
                "https://telemetry.ekstern.dev.nav.no/collect");
        var client = WebTestClient.bindToRouterFunction(router).build();

        for (var path : new String[]{"/", "/index.html", "/login", "/gruppe/123", "/oversikt", "/maler", "/statistikk"}) {
            client.get().uri(path).exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                    .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "no-store")
                    .expectBody(String.class).value(body -> assertThat(body).contains(
                            "<meta name=\"dolly-telemetry-enabled\" content=\"true\">",
                            "<meta name=\"nais-app\" content=\"" + appName + "\">",
                            "<meta name=\"nais-team\" content=\"dolly\">",
                            "<meta name=\"nais-cluster\" content=\"dev-gcp\">",
                            "<meta name=\"nais-telemetry-url\" content=\"https://telemetry.ekstern.dev.nav.no/collect\">",
                            "<title>Dolly</title>",
                            "<div id=\"root\"></div>"));
        }
    }

    @Test
    void shouldRenderDisabledTelemetryWithoutChangingPageContent() {
        var router = controller.htmlRouter(html, false, "dolly-frontend", "dolly", "local", "");

        WebTestClient.bindToRouterFunction(router).build()
                .get().uri("/").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(body -> assertThat(body).contains(
                        "<meta name=\"dolly-telemetry-enabled\" content=\"false\">",
                        "<div id=\"root\"></div>"));
    }

    @Test
    void shouldEscapeMetadataAttributeValues() {
        var router = controller.htmlRouter(html, false, "dolly\"<script>", "dolly", "dev-gcp",
                "https://collector.example/collect?a=1&b=2");

        WebTestClient.bindToRouterFunction(router).build()
                .get().uri("/").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(body -> assertThat(body)
                        .contains("dolly&quot;&lt;script&gt;", "a=1&amp;b=2")
                        .doesNotContain("<script>"));
    }

    @Test
    void shouldNotReadFrontendBundleDuringBackendStartup() {
        var router = controller.htmlRouter(new ClassPathResource("missing-frontend-index.html"),
                false, "dolly-frontend", "dolly", "local", "");

        assertThat(router).isNotNull();
    }

    @Test
    void shouldNotRouteApiRequestsToHtml() {
        var router = controller.htmlRouter(html, false, "dolly-frontend", "dolly", "local", "");

        WebTestClient.bindToRouterFunction(router).build()
                .get().uri("/api/test").exchange()
                .expectStatus().isNotFound();
    }
}
