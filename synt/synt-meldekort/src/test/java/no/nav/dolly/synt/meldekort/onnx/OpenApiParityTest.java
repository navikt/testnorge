package no.nav.dolly.synt.meldekort.onnx;

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
    void shouldExposeLegacyCompatibleOpenApiContract() throws IOException {

        var currentSpec = readCurrentSpec();
        var legacySpec = readLegacySpec();

        assertOpenApiVersionTolerance(currentSpec, legacySpec);

        assertThat(fieldNames(currentSpec.path("paths")))
                .isEqualTo(fieldNames(legacySpec.path("paths")));
        assertThat(currentSpec.path("info"))
                .isEqualTo(legacySpec.path("info"));
        assertServerTolerance(currentSpec, legacySpec);
        assertThat(currentSpec.path("tags"))
                .isEqualTo(legacySpec.path("tags"));
        assertThat(currentSpec.at("/components/securitySchemes/jwt"))
                .isEqualTo(legacySpec.at("/components/securitySchemes/jwt"));

        assertMeldekortTypeTolerance(currentSpec, legacySpec);

        var currentOperation = currentSpec.at("/paths/~1meldekort~1{meldegruppe}~1{num_to_generate}/get");
        var legacyOperation = legacySpec.at("/paths/~1meldekort~1{meldegruppe}~1{num_to_generate}/get");

        assertThat(currentOperation.path("operationId").asText())
                .isEqualTo(legacyOperation.path("operationId").asText());
        assertThat(currentOperation.path("summary").asText())
                .isEqualTo(legacyOperation.path("summary").asText());
        assertThat(currentOperation.path("security"))
                .isEqualTo(legacyOperation.path("security"));

        var currentResponse = currentOperation.at("/responses/200");
        var legacyResponse = legacyOperation.at("/responses/200");

        assertThat(currentResponse.path("description").asText())
                .isEqualTo(legacyResponse.path("description").asText());
        assertThat(normalizeSchema(currentResponse.at("/content/application~1json/schema"), currentSpec.path("components").path("schemas")))
                .isEqualTo(normalizeSchema(legacyResponse.at("/content/application~1json/schema"), legacySpec.path("components").path("schemas")));

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
        assertThat(body).isNotBlank();
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
        node
                .fieldNames()
                .forEachRemaining(fieldNames::add);
        return fieldNames;

    }

    private JsonNode normalizeSchema(JsonNode schemaNode, JsonNode components) {

        var resolvedSchema = resolveSchema(schemaNode, components);
        var normalized = objectMapper.createObjectNode();

        var type = resolvedSchema.path("type").asText(null);
        if (type == null && resolvedSchema.has("properties")) {
            type = "object";
        }
        if (type == null && resolvedSchema.has("items")) {
            type = "array";
        }
        if (type != null) {
            normalized.put("type", type);
        }
        if (resolvedSchema.has("enum")) {
            normalized.set("enum", resolvedSchema.path("enum").deepCopy());
        }
        if (resolvedSchema.has("properties")) {
            var normalizedProperties = objectMapper.createObjectNode();
            fieldNames(resolvedSchema.path("properties"))
                    .forEach(propertyName -> normalizedProperties
                            .set(
                                    propertyName,
                                    normalizeSchema(resolvedSchema.path("properties").path(propertyName), components)
                            )
                    );
            normalized.set("properties", normalizedProperties);
        }
        if (resolvedSchema.has("items")) {
            normalized.set("items", normalizeSchema(resolvedSchema.path("items"), components));
        }
        return normalized;

    }

    private JsonNode resolveSchema(JsonNode schemaNode, JsonNode components) {

        var resolvedSchema = schemaNode;
        while (resolvedSchema.has("$ref")) {
            var reference = resolvedSchema.path("$ref").asText();
            var schemaName = reference.substring(reference.lastIndexOf('/') + 1);
            resolvedSchema = components.path(schemaName);
        }
        return resolvedSchema;

    }

    private void assertOpenApiVersionTolerance(JsonNode generatedSpec, JsonNode legacySpec) {

        var generatedVersion = generatedSpec.path("openapi").asText();
        var legacyVersion = legacySpec.path("openapi").asText();
        assertThat(generatedVersion)
                .isNotBlank()
                .startsWith("3.0.");
        assertThat(legacyVersion)
                .isNotBlank()
                .startsWith("3.0.");

    }

    private void assertMeldekortTypeTolerance(JsonNode generatedSpec, JsonNode legacySpec) {

        var generatedType = generatedSpec.at("/components/schemas/MeldekortType");
        var legacyType = legacySpec.at("/components/schemas/MeldekortType");
        var generatedParameterSchema = generatedSpec.at("/paths/~1meldekort~1{meldegruppe}~1{num_to_generate}/get/parameters/0/schema");
        if (generatedType.isMissingNode()) {
            assertThat(normalizeSchema(generatedParameterSchema, generatedSpec.path("components").path("schemas")))
                    .isEqualTo(normalizeSchema(legacyType, legacySpec.path("components").path("schemas")));
            return;
        }

        assertThat(normalizeSchema(generatedType, generatedSpec.path("components").path("schemas")))
                .isEqualTo(normalizeSchema(legacyType, legacySpec.path("components").path("schemas")));

    }

    private void assertServerTolerance(JsonNode generatedSpec, JsonNode legacySpec) {

        var generatedServers = generatedSpec.path("servers");
        var legacyServers = legacySpec.path("servers");

        assertThat(generatedServers.isArray()).isTrue();
        assertThat(generatedServers.isEmpty()).isFalse();

        var generatedUrl = generatedServers.get(0).path("url").asText();
        var legacyUrl = legacyServers.isArray() && !legacyServers.isEmpty()
                ? legacyServers.get(0).path("url").asText()
                : "";

        assertThat(generatedUrl)
                .isIn("/api/v1", legacyUrl);

    }

}

