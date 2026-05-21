package no.nav.dolly.synt.aap.onnx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class OpenApiParityTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OnnxService onnxService;

    @Test
    void shouldExposeLegacyCompatibleOpenApiContract()
            throws IOException {

        var currentSpec = readCurrentSpec();
        var legacySpec = readLegacySpec();

        assertThat(currentSpec.path("openapi").asText()).startsWith("3.0.");
        assertThat(legacySpec.path("openapi").asText()).startsWith("3.0.");

        assertThat(normalizePaths(fieldNames(currentSpec.path("paths"))))
                .containsAll(fieldNames(legacySpec.path("paths")));

        assertOperationId(currentSpec, "/aap", "post", "service.generator.get_synthetic_aap");
        assertOperationId(currentSpec, "/aap/filtered", "post", "service.generator.get_filtered_synthetic_aap");
        assertOperationId(currentSpec, "/11_5", "post", "service.generator.get_synthetic_11_5");
        assertOperationId(currentSpec, "/fri_mk", "post", "service.generator.get_synthetic_fri_mk");
        assertOperationId(currentSpec, "/aaungufor", "post", "service.generator.get_synthetic_aaungufor");
        assertOperationId(currentSpec, "/aatfor", "post", "service.generator.get_synthetic_aatfor");

    }

    @Test
    void shouldNotExposeLegacyOpenApiResource() {
        webTestClient
                .get()
                .uri("/old-api/openapi.json")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    private JsonNode readCurrentSpec()
            throws IOException {

        var body = webTestClient
                .get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(body)
                .isNotBlank();
        return objectMapper.readTree(body);

    }

    private JsonNode readLegacySpec()
            throws IOException {

        try (InputStream inputStream = new ClassPathResource("old-api/openapi.json").getInputStream()) {
            return objectMapper.readTree(inputStream);
        }

    }

    private TreeSet<String> fieldNames(JsonNode node) {

        var fieldNames = new TreeSet<String>();
        node.fieldNames().forEachRemaining(fieldNames::add);
        return fieldNames;

    }

    private TreeSet<String> normalizePaths(TreeSet<String> paths) {

        var normalized = new TreeSet<String>();
        for (var path : paths) {
            if (path.startsWith("/api/v1")) {
                normalized.add(path.substring("/api/v1".length()));
            } else {
                normalized.add(path);
            }
        }
        return normalized;

    }

    private void assertOperationId(JsonNode currentSpec, String path, String method, String expectedOperationId) {

        var operation = currentSpec.path("paths").path(path).path(method);
        if (operation.isMissingNode()) {
            operation = currentSpec.path("paths").path("/api/v1" + path).path(method);
        }
        assertThat(operation.path("operationId").asText()).isEqualTo(expectedOperationId);

    }

}

