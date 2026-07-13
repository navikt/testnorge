package no.nav.dolly.synt.aap.onnx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;
import no.nav.dolly.synt.aap.dto.VilkaarDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Note that this test requires manually downloaded models in {@code src/main/resources/models} and is intended as a smoke
 * test to verify that the generator produces output that at least superficially resembles real vedtak – it is not intended
 * to verify that the output is correct in any detailed way.
 */
class AapVedtakGeneratorTest {

    private static final Path MODEL_DIRECTORY = Path.of("src/main/resources/models").toAbsolutePath().normalize();
    private static final String DEFAULT_REQUEST_RESOURCE = "requests/default-request.json";
    private static final String FILTERED_REQUEST_RESOURCE = "requests/filtered-es-chain-request.json";
    private static final String TILDATO_FALSE_REQUEST_RESOURCE = "requests/tildato-false-request.json";

    private static final Set<String> VALID_UTFALL = Set.of("JA", "NEI");
    private static final Set<String> VALID_VEDTAKTYPE = Set.of("O", "E", "S", "G");
    private static final Set<String> VALID_BOOLEAN_VILKAAR_STATUS = Set.of("J", "N");

    private static AapVedtakGenerator generator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll() {
        assumeTrue(Files.isDirectory(MODEL_DIRECTORY), "Models directory not found – skipping smoke tests: " + MODEL_DIRECTORY);
        assumeTrue(Objects.nonNull(AapVedtakGeneratorTest.class.getResource("/" + DEFAULT_REQUEST_RESOURCE)), "Request resources not found – skipping smoke tests");
        generator = new AapVedtakGenerator(MODEL_DIRECTORY);
    }

    @Test
    void shouldGeneratePlausibleAapVedtakFromDefaultRequest()
            throws IOException {

        var requests = loadRequests(DEFAULT_REQUEST_RESOURCE);
        var result = generator
                .generateVedtak(AapModelType.AAP, requests, true)
                .stream()
                .map(AapVedtakMapper::toAapVedtak)
                .toList();

        assertThat(result).hasSameSizeAs(requests);
        result.forEach(this::assertPlausibleAapVedtak);

    }

    @Test
    void shouldOmitTilDatoWhenBrukInnsendtTilDatoIsFalse()
            throws IOException {

        var requests = loadRequests(TILDATO_FALSE_REQUEST_RESOURCE);
        var result = generator
                .generateVedtak(AapModelType.AAP, requests, false)
                .stream()
                .map(AapVedtakMapper::toAapVedtak)
                .toList();

        assertThat(result).hasSameSizeAs(requests);
        result.forEach(vedtak -> assertThat(vedtak.getTilDato()).isNullOrEmpty());

    }

    @Test
    void shouldGeneratePlausibleAapVedtakAfterHistoryFiltering()
            throws IOException {

        var filteredRequests = AapHistoryFilter.removeIllogicalRequests(loadRequests(FILTERED_REQUEST_RESOURCE));
        var generated = generator
                .generateVedtak(AapModelType.AAP, filteredRequests, true)
                .stream()
                .map(AapVedtakMapper::toAapVedtak)
                .toList();
        var result = AapHistoryFilter.postprocessForUseInHistory(generated);

        assertThat(result).isNotEmpty();
        result.forEach(this::assertPlausibleAapVedtak);

    }

    @Test
    @Disabled("This is a very slow smoke test due to the models involved – enable if needed")
    void shouldGeneratePlausibleVedtak115()
            throws IOException {

        assumeTrue(hasModelFilesForType(AapModelType.AAP_115), "No AAP_115 model files – skipping 11.5 smoke test");

        var requests = loadRequests(DEFAULT_REQUEST_RESOURCE);
        var result = generator
                .generateVedtak(AapModelType.AAP_115, requests, true)
                .stream()
                .map(AapVedtakMapper::toVedtak115)
                .toList();

        assertThat(result).hasSameSizeAs(requests);
        result.forEach(this::assertPlausibleVedtak115);

    }

    @Test
    void shouldGeneratePlausibleAatforVedtak()
            throws IOException {

        assumeTrue(hasModelFilesForType(AapModelType.AATFOR), "No AATFOR model files – skipping aatfor smoke test");

        var requests = loadRequests(DEFAULT_REQUEST_RESOURCE);
        var result = generator
                .generateVedtak(AapModelType.AATFOR, requests, true)
                .stream()
                .map(AapVedtakMapper::toAatforAaunguforFriMkVedtak)
                .toList();

        assertThat(result).hasSameSizeAs(requests);
        result.forEach(this::assertPlausibleAlternativeVedtak);

    }

    @Test
    void shouldGeneratePlausibleAaunguforVedtak()
            throws IOException {

        assumeTrue(hasModelFilesForType(AapModelType.AAUNGUFOR), "No AAUNGUFOR model files – skipping aaungufor smoke test");

        var requests = loadRequests(DEFAULT_REQUEST_RESOURCE);
        var result = generator
                .generateVedtak(AapModelType.AAUNGUFOR, requests, true)
                .stream()
                .map(AapVedtakMapper::toAatforAaunguforFriMkVedtak)
                .toList();

        assertThat(result).hasSameSizeAs(requests);
        result.forEach(this::assertPlausibleAlternativeVedtak);

    }

    @Test
    void shouldGeneratePlausibleFriMkVedtak()
            throws IOException {

        assumeTrue(hasModelFilesForType(AapModelType.FRI_MK), "No FRI_MK model files – skipping fri_mk smoke test");

        var requests = loadRequests(DEFAULT_REQUEST_RESOURCE);
        var result = generator.generateVedtak(AapModelType.FRI_MK, requests, true)
                .stream()
                .map(AapVedtakMapper::toAatforAaunguforFriMkVedtak)
                .toList();

        assertThat(result).hasSameSizeAs(requests);
        result.forEach(this::assertPlausibleAlternativeVedtak);

    }

    private void assertPlausibleAapVedtak(AapVedtakDto vedtak) {

        assertThat(vedtak.getFraDato())
                .as("fraDato must be a valid ISO date")
                .isNotBlank()
                .satisfies(this::assertParsesAsDate);
        assertThat(vedtak.getUtfall())
                .as("utfall must be JA or NEI")
                .isIn(VALID_UTFALL);
        assertThat(vedtak.getVedtaktype())
                .as("vedtaktype must be a known type code")
                .isIn(VALID_VEDTAKTYPE);
        assertThat(vedtak.getDatoMottatt())
                .as("datoMottatt must be set")
                .isNotBlank();
        assertThat(vedtak.getVedtakDato())
                .as("vedtakDato must be set for AAP")
                .isNotBlank();
        assertThat(vedtak.getAktfasekode())
                .as("aktfasekode must be set for AAP")
                .isNotBlank();

        if (vedtak.getVilkaar() != null) {
            vedtak.getVilkaar().forEach(this::assertPlausibleBooleanVilkaar);
        }

    }

    private void assertPlausibleVedtak115(Vedtak115Dto vedtak) {

        assertThat(vedtak.getFraDato())
                .as("fraDato must be a valid ISO date")
                .isNotBlank()
                .satisfies(this::assertParsesAsDate);
        assertThat(vedtak.getUtfall())
                .as("utfall must be JA or NEI")
                .isIn(VALID_UTFALL);
        assertThat(vedtak.getVedtaktype())
                .as("vedtaktype must be a known type code")
                .isIn(VALID_VEDTAKTYPE);
        assertThat(vedtak.getDatoMottatt())
                .as("datoMottatt must be set")
                .isNotBlank();

        if (vedtak.getVilkaar() != null) {
            vedtak.getVilkaar().forEach(this::assertPlausibleBooleanVilkaar);
        }

    }

    private void assertPlausibleAlternativeVedtak(AatforAaunguforFriMkVedtakDto vedtak) {

        assertThat(vedtak.getFraDato())
                .as("fraDato must be a valid ISO date")
                .isNotBlank()
                .satisfies(this::assertParsesAsDate);
        assertThat(vedtak.getUtfall())
                .as("utfall must be JA or NEI")
                .isIn(VALID_UTFALL);
        assertThat(vedtak.getVedtaktype())
                .as("vedtaktype must be a known type code")
                .isIn(VALID_VEDTAKTYPE);
        assertThat(vedtak.getDatoMottatt())
                .as("datoMottatt must be set")
                .isNotBlank();

        if (vedtak.getVilkaar() != null) {
            vedtak.getVilkaar().forEach(this::assertPlausibleBooleanVilkaar);
        }

    }

    private void assertPlausibleBooleanVilkaar(VilkaarDto vilkaar) {

        assertThat(vilkaar.getKode()).isNotBlank();
        assertThat(vilkaar.getStatus())
                .as("vilkaar status for %s must be a boolean marker", vilkaar.getKode())
                .isIn(VALID_BOOLEAN_VILKAAR_STATUS);

    }

    private void assertParsesAsDate(String value) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new AssertionError("Expected a valid ISO date but got: " + value, e);
        }
    }

    private List<VedtakRequestDto> loadRequests(String resourceName)
            throws IOException {

        var resourceUrl = getClass().getResource("/" + resourceName);
        if (resourceUrl == null) {
            throw new FileNotFoundException("Resource not found: " + resourceName);
        }
        try (var inputStream = resourceUrl.openStream()) {
            var content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return objectMapper.readValue(content, new TypeReference<>() {
            });
        }

    }

    private boolean hasModelFilesForType(AapModelType modelType) {
        try (var files = Files.list(MODEL_DIRECTORY)) {
            return files
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> path.getFileName().toString().startsWith(modelType.name() + "_"));
        } catch (IOException e) {
            return false;
        }
    }

}







