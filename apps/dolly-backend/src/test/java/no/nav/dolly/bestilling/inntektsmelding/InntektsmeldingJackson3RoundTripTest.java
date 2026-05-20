package no.nav.dolly.bestilling.inntektsmelding;

import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.bestilling.inntektsmelding.domain.TransaksjonMappingDTO;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsKontaktinformasjon;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.dto.jackson.v1.CaseInsensitiveEnumModule;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InntektsmeldingJackson3RoundTripTest {

    private final JsonMapper objectMapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();

    private final JsonMapper serviceObjectMapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .addModule(new CaseInsensitiveEnumModule())
            .build();

    @Test
    void shouldDeserializeServiceResponseIntoDollyBackendResponse() {
        var serviceResponseJson = """
                {
                  "fnr": "12345678901",
                  "dokumenter": [
                    {
                      "journalpostId": "JP-123",
                      "dokumentInfoId": "DI-456",
                      "xml": "<some-large-xml-content/>"
                    }
                  ]
                }
                """;

        var response = objectMapper.readValue(serviceResponseJson, InntektsmeldingResponse.class);

        assertThat(response.getFnr()).isEqualTo("12345678901");
        assertThat(response.getDokumenter()).hasSize(1);
        assertThat(response.getDokumenter().getFirst().getJournalpostId()).isEqualTo("JP-123");
        assertThat(response.getDokumenter().getFirst().getDokumentInfoId()).isEqualTo("DI-456");
        assertThat(response.getError()).isNull();
        assertThat(response.getStatus()).isNull();
    }

    @Test
    void shouldSerializeInntektsmeldingRequestWithAltinnEnums() {
        var request = InntektsmeldingRequest.builder()
                .miljoe("q1")
                .arbeidstakerFnr("12345678901")
                .inntekter(List.of(
                        RsInntektsmeldingRequest.builder()
                                .ytelse(YtelseKodeListe.FORELDREPENGER)
                                .aarsakTilInnsending(AarsakInnsendingKodeListe.NY)
                                .arbeidsgiver(RsArbeidsgiver.builder()
                                        .virksomhetsnummer("947064649")
                                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                                .kontaktinformasjonNavn("Test")
                                                .telefonnummer("99999999")
                                                .build())
                                        .build())
                                .avsendersystem(RsAvsendersystem.builder()
                                        .systemnavn("Dolly")
                                        .systemversjon("2.0")
                                        .innsendingstidspunkt(LocalDateTime.of(2024, 2, 9, 4, 49, 13))
                                        .build())
                                .build()
                ))
                .build();

        var json = objectMapper.writeValueAsString(request);

        assertThat(json).contains("\"ytelse\":\"Foreldrepenger\"");
        assertThat(json).contains("\"aarsakTilInnsending\":\"Ny\"");
        assertThat(json).contains("\"arbeidstakerFnr\":\"12345678901\"");
        assertThat(json).contains("\"miljoe\":\"q1\"");

        var deserialized = objectMapper.readValue(json, InntektsmeldingRequest.class);
        assertThat(deserialized.getInntekter()).hasSize(1);
        assertThat(deserialized.getInntekter().getFirst().getYtelse()).isEqualTo(YtelseKodeListe.FORELDREPENGER);
        assertThat(deserialized.getInntekter().getFirst().getAarsakTilInnsending()).isEqualTo(AarsakInnsendingKodeListe.NY);
    }

    @Test
    void shouldSerializeTransaksjonMappingDTO() {
        var dto = TransaksjonMappingDTO.builder()
                .request(InntektsmeldingRequest.builder()
                        .arbeidstakerFnr("12345678901")
                        .inntekter(List.of(
                                RsInntektsmeldingRequest.builder()
                                        .ytelse(YtelseKodeListe.PLEIEPENGER_BARN)
                                        .aarsakTilInnsending(AarsakInnsendingKodeListe.NY)
                                        .build()
                        ))
                        .miljoe("q1")
                        .build())
                .dokument(InntektsmeldingResponse.Dokument.builder()
                        .journalpostId("JP-789")
                        .dokumentInfoId("DI-012")
                        .build())
                .build();

        var json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"ytelse\":\"PleiepengerBarn\"");
        assertThat(json).contains("\"journalpostId\":\"JP-789\"");
        assertThat(json).contains("\"dokumentInfoId\":\"DI-012\"");

        var deserialized = objectMapper.readValue(json, TransaksjonMappingDTO.class);
        assertThat(deserialized.getDokument().getJournalpostId()).isEqualTo("JP-789");
        assertThat(deserialized.getRequest().getInntekter().getFirst().getYtelse())
                .isEqualTo(YtelseKodeListe.PLEIEPENGER_BARN);
    }

    @Test
    void shouldDeserializeResponseWithEmptyDokumenter() {
        var json = """
                {
                  "fnr": "12345678901",
                  "dokumenter": []
                }
                """;

        var response = objectMapper.readValue(json, InntektsmeldingResponse.class);

        assertThat(response.getFnr()).isEqualTo("12345678901");
        assertThat(response.getDokumenter()).isEmpty();
    }

    @Test
    void shouldDeserializeAllYtelseKodeListeValues() {
        for (YtelseKodeListe ytelse : YtelseKodeListe.values()) {
            var json = objectMapper.writeValueAsString(ytelse);
            assertThat(json).isEqualTo("\"" + ytelse.getValue() + "\"");

            var deserialized = objectMapper.readValue(json, YtelseKodeListe.class);
            assertThat(deserialized)
                    .as("Round-trip for %s (serialized as %s)", ytelse.name(), json)
                    .isEqualTo(ytelse);
        }
    }

    @Test
    void shouldDeserializeAllYtelseKodeListeValuesWithCaseInsensitiveModule() {
        for (YtelseKodeListe ytelse : YtelseKodeListe.values()) {
            var json = objectMapper.writeValueAsString(ytelse);

            var deserialized = serviceObjectMapper.readValue(json, YtelseKodeListe.class);
            assertThat(deserialized)
                    .as("CaseInsensitiveEnum round-trip for %s (serialized as %s)", ytelse.name(), json)
                    .isEqualTo(ytelse);
        }
    }

    @Test
    void shouldSimulateCrossServiceSerializationFlow() {
        var request = InntektsmeldingRequest.builder()
                .miljoe("q1")
                .arbeidstakerFnr("12345678901")
                .inntekter(List.of(
                        RsInntektsmeldingRequest.builder()
                                .ytelse(YtelseKodeListe.PLEIEPENGER_BARN)
                                .aarsakTilInnsending(AarsakInnsendingKodeListe.NY)
                                .arbeidsgiver(RsArbeidsgiver.builder()
                                        .virksomhetsnummer("947064649")
                                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                                .kontaktinformasjonNavn("Test Testesen")
                                                .telefonnummer("99999999")
                                                .build())
                                        .build())
                                .avsendersystem(RsAvsendersystem.builder()
                                        .systemnavn("Dolly")
                                        .systemversjon("2.0")
                                        .innsendingstidspunkt(LocalDateTime.of(2024, 2, 9, 4, 49, 13))
                                        .build())
                                .build()
                ))
                .build();

        var sentJson = objectMapper.writeValueAsString(request);

        assertThat(sentJson)
                .as("WebClient serialized JSON should contain PleiepengerBarn (from @JsonValue)")
                .contains("\"ytelse\":\"PleiepengerBarn\"");
        assertThat(sentJson).contains("\"aarsakTilInnsending\":\"Ny\"");
        assertThat(sentJson).contains("\"inntekter\":[{");

        var received = serviceObjectMapper.readValue(sentJson, InntektsmeldingRequest.class);
        assertThat(received.getInntekter())
                .as("Service should receive non-empty inntekter list")
                .hasSize(1);
        assertThat(received.getInntekter().getFirst().getYtelse())
                .as("Ytelse enum should survive cross-service serialization")
                .isEqualTo(YtelseKodeListe.PLEIEPENGER_BARN);
        assertThat(received.getInntekter().getFirst().getAarsakTilInnsending())
                .isEqualTo(AarsakInnsendingKodeListe.NY);
        assertThat(received.getInntekter().getFirst().getArbeidsgiver().getVirksomhetsnummer())
                .isEqualTo("947064649");
        assertThat(received.getMiljoe()).isEqualTo("q1");
        assertThat(received.getArbeidstakerFnr()).isEqualTo("12345678901");
    }
}
