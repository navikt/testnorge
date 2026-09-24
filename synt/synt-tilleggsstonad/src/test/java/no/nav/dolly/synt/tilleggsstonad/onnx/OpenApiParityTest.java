package no.nav.dolly.synt.tilleggsstonad.onnx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class OpenApiParityTest {

    private static final Map<String, String> EXPECTED_POST_OPERATION_IDS = Map.ofEntries(
            Map.entry("/boutgift", "service.generator.get_synthetic_boutgift"),
            Map.entry("/boutgifter_arbeidssokere", "service.generator.get_synthetic_boutgifter_arbeidssokere"),
            Map.entry("/daglig_reise", "service.generator.get_synthetic_daglig_reise"),
            Map.entry("/daglig_reise_arbeidssoker", "service.generator.get_synthetic_daglig_reise_arbeidssoker"),
            Map.entry("/laeremidler", "service.generator.get_synthetic_laeremidler"),
            Map.entry("/laeremidler_arbeidssokere", "service.generator.get_synthetic_laeremidler_arbeidssokere"),
            Map.entry("/flytting", "service.generator.get_synthetic_flytting"),
            Map.entry("/flytting_arbeidssokere", "service.generator.get_synthetic_flytting_arbeidssokere"),
            Map.entry("/reise_aktivitet_og_hjemreiser", "service.generator.get_synthetic_reise_akt"),
            Map.entry("/reise_aktivitet_og_hjemreiser_arbeidssokere", "service.generator.get_synthetic_reise_akt_arbeidssokere"),
            Map.entry("/reisestonad_til_arbeidssokere", "service.generator.get_synthetic_reisestonad_til_arbeidssokere"),
            Map.entry("/reise_til_obligatorisk_samling", "service.generator.get_synthetic_reise_til_obligatorisk_samling"),
            Map.entry("/reise_til_obligatorisk_samling_arbeidssokere", "service.generator.get_synthetic_reise_til_obligatorisk_samling_arbeidssokere"),
            Map.entry("/tilsyn_familiemedlemmer", "service.generator.get_synthetic_tilsyn_familiemedlemmer"),
            Map.entry("/tilsyn_familiemedlemmer_arbeidssokere", "service.generator.get_synthetic_tilsyn_familiemedlemmer_arbeidssokere"),
            Map.entry("/tilsyn_barn", "service.generator.get_synthetic_tilsyn_barn"),
            Map.entry("/tilsyn_barn_arbeidssoker", "service.generator.get_synthetic_tilsyn_barn_arbeidssoker")
    );

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OnnxService onnxService;

    @Test
    void shouldExposeTilleggsstonadOpenApiContract() throws IOException {

        var currentSpec = readCurrentSpec();

        assertThat(currentSpec.path("openapi").asText()).startsWith("3.0.");
        assertThat(currentSpec.path("info").path("title").asText()).isEqualTo("Syntetisering - Tilleggsstonad");
        assertThat(currentSpec.path("info").path("description").asText()).isEqualTo("Applikasjonen genererer syntetiske Arena Tilleggsstonad-vedtak.");
        assertThat(currentSpec.path("servers").get(0).path("url").asText()).isEqualTo("/api/v1");
        assertThat(currentSpec.path("tags").get(0).path("name").asText()).isEqualTo("Syntetisering");
        assertThat(currentSpec.at("/components/securitySchemes/jwt").isMissingNode()).isFalse();

        assertThat(normalizePaths(fieldNames(currentSpec.path("paths"))))
                .isEqualTo(new TreeSet<>(EXPECTED_POST_OPERATION_IDS.keySet()));

        EXPECTED_POST_OPERATION_IDS.forEach((path, expectedOperationId) -> {
            var operation = resolveOperation(currentSpec, path);
            assertThat(operation.path("operationId").asText()).isEqualTo(expectedOperationId);
            assertThat(operation.path("security").isArray()).isTrue();
            assertThat(operation.path("requestBody").path("required").asBoolean()).isTrue();
            assertThat(operation.path("responses").path("200").isMissingNode()).isFalse();
        });

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

    private TreeSet<String> fieldNames(JsonNode node) {

        var fieldNames = new TreeSet<String>();
        node
                .fieldNames()
                .forEachRemaining(fieldNames::add);
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

    private JsonNode resolveOperation(JsonNode currentSpec, String path) {

        var operation = currentSpec.path("paths").path(path).path("post");
        if (operation.isMissingNode()) {
            operation = currentSpec.path("paths").path("/api/v1" + path).path("post");
        }
        return operation;

    }

}
