package no.nav.dolly.synt.dagpenger.onnx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class OpenApiParityTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DagpengevedtakGenerator dagpengevedtakGenerator;

    @Test
    void shouldExposeLegacyCompatibleOpenApiContract() throws IOException {

        var currentSpec = readCurrentSpec();
        var legacySpec = readLegacySpec();

        assertOpenApiVersionTolerance(currentSpec, legacySpec);

        assertThat(fieldNames(currentSpec.path("paths")))
                .isEqualTo(fieldNames(legacySpec.path("paths")));
        assertThat(currentSpec.path("info"))
                .isEqualTo(legacySpec.path("info"));
        assertThat(currentSpec.path("servers"))
                .isEqualTo(legacySpec.path("servers"));
        assertThat(currentSpec.path("tags"))
                .isEqualTo(legacySpec.path("tags"));
        assertThat(currentSpec.at("/components/securitySchemes/jwt"))
                .isEqualTo(legacySpec.at("/components/securitySchemes/jwt"));

        assertRettighetTypeTolerance(currentSpec, legacySpec);
        assertComponentSchemaTolerance(currentSpec, legacySpec);

        var currentOperation = currentSpec.at("/paths/~1vedtak~1{rettighet}/post");
        var legacyOperation = legacySpec.at("/paths/~1vedtak~1{rettighet}/post");

        assertThat(currentOperation.path("operationId").asText())
                .isEqualTo(legacyOperation.path("operationId").asText());
        assertThat(currentOperation.path("summary").asText())
                .isEqualTo(legacyOperation.path("summary").asText());
        assertThat(currentOperation.path("security"))
                .isEqualTo(legacyOperation.path("security"));

        var currentRequestMediaType = currentOperation.at("/requestBody/content/application~1json");
        var legacyRequestMediaType = legacyOperation.at("/requestBody/content/application~1json");

        assertThat(currentOperation.at("/requestBody/description").asText())
                .isEqualTo(legacyOperation.at("/requestBody/description").asText());
        assertThat(currentOperation.at("/requestBody/required").asBoolean())
                .isEqualTo(legacyOperation.at("/requestBody/required").asBoolean());
        assertThat(normalizeSchema(currentRequestMediaType.path("schema"), currentSpec.path("components").path("schemas")))
                .isEqualTo(normalizeSchema(legacyRequestMediaType.path("schema"), legacySpec.path("components").path("schemas")));
        assertThat(normalizeExample(currentRequestMediaType))
                .isEqualTo(normalizeExample(legacyRequestMediaType));

        var currentParameter = currentOperation.path("parameters").get(0);
        var legacyParameter = legacyOperation.path("parameters").get(0);

        assertThat(currentParameter.path("in").asText())
                .isEqualTo(legacyParameter.path("in").asText());
        assertThat(currentParameter.path("name").asText())
                .isEqualTo(legacyParameter.path("name").asText());
        assertThat(currentParameter.path("required").asBoolean())
                .isEqualTo(legacyParameter.path("required").asBoolean());
        assertThat(currentParameter.path("description").asText())
                .isEqualTo(legacyParameter.path("description").asText());
        assertThat(normalizeSchema(currentParameter.path("schema"), currentSpec.path("components").path("schemas")))
                .isEqualTo(normalizeSchema(legacyParameter.path("schema"), legacySpec.path("components").path("schemas")));

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
                .uri("/api/api-docs")
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

    private JsonNode normalizeExample(JsonNode mediaType) {

        if (mediaType.path("schema").has("example")) {
            return normalizeExampleValue(mediaType.path("schema").path("example"));
        }
        if (mediaType.has("example")) {
            return normalizeExampleValue(mediaType.path("example"));
        }
        if (mediaType.has("examples") && mediaType.path("examples").isObject()) {
            var exampleNames = new ArrayList<String>();
            mediaType
                    .path("examples")
                    .fieldNames()
                    .forEachRemaining(exampleNames::add);
            if (!exampleNames.isEmpty()) {
                return normalizeExampleValue(mediaType.path("examples").path(exampleNames.getFirst()));
            }
        }
        return MissingNode.getInstance();

    }

    private JsonNode normalizeExampleValue(JsonNode exampleNode) {

        var valueNode = exampleNode.has("value") ? exampleNode.path("value") : exampleNode;
        if (!valueNode.isTextual()) {
            return valueNode;
        }
        var value = valueNode.asText();
        try {
            return objectMapper.readTree(value);
        } catch (IOException ignored) {
            return valueNode;
        }

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

    private void assertRettighetTypeTolerance(JsonNode generatedSpec, JsonNode legacySpec) {

        var generatedRettighetType = generatedSpec.at("/components/schemas/RettighetType");
        var legacyRettighetType = legacySpec.at("/components/schemas/RettighetType");
        var generatedParameterSchema = generatedSpec.at("/paths/~1vedtak~1{rettighet}/post/parameters/0/schema");
        if (generatedRettighetType.isMissingNode()) {
            assertThat(normalizeSchema(generatedParameterSchema, generatedSpec.path("components").path("schemas")))
                    .isEqualTo(normalizeSchema(legacyRettighetType, legacySpec.path("components").path("schemas")));
            return;
        }

        assertThat(normalizeSchema(generatedRettighetType, generatedSpec.path("components").path("schemas")))
                .isEqualTo(normalizeSchema(legacyRettighetType, legacySpec.path("components").path("schemas")));

    }

    private void assertComponentSchemaTolerance(JsonNode generatedSpec, JsonNode legacySpec) {

        var generatedSchemaNames = fieldNames(generatedSpec.path("components").path("schemas"));
        var legacySchemaNames = fieldNames(legacySpec.path("components").path("schemas"));
        assertThat(generatedSchemaNames)
                .containsAll(legacySchemaNames.stream().filter(name -> !name.equals("RettighetType")).toList());

    }

}

