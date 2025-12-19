package no.nav.registre.testnav.inntektsmeldingservice;

import tools.jackson.databind.ObjectMapper;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class JsonDeserializationTest {

    private static final String INNTEKTSMELDING_JSON = """
            {
              "miljoe" : "q1",
              "arbeidstakerFnr" : "02889097828",
              "joarkMetadata" : {
                "avsenderMottakerIdType" : "ORGNR",
                "tema" : "OMS"
              },
              "inntekter" : [ {
                "ytelse" : "PleiepengerBarn",
                "aarsakTilInnsending" : "Ny",
                "naerRelasjon" : false,
                "avsendersystem" : {
                  "systemnavn" : "Dolly",
                  "systemversjon" : "2.0",
                  "innsendingstidspunkt" : "2024-02-09T04:49:13"
                },
                "arbeidsgiver" : {
                  "virksomhetsnummer" : "947064649",
                  "kontaktinformasjon" : {
                    "kontaktinformasjonNavn" : "Dolly Dollesen",
                    "telefonnummer" : "99999999"
                  }
                },
                "arbeidsforhold" : {
                  "arbeidsforholdId" : "",
                  "foersteFravaersdag" : "2023-12-04T00:00:00",
                  "beregnetInntekt" : {
                    "beloep" : 40000.0
                  },
                  "avtaltFerieListe" : [ ],
                  "utsettelseAvForeldrepengerListe" : [ ],
                  "graderingIForeldrepengerListe" : [ ]
                },
                "refusjon" : {
                  "refusjonsbeloepPrMnd" : 40000.0,
                  "endringIRefusjonListe" : [ ]
                },
                "opphoerAvNaturalytelseListe" : [ ],
                "gjenopptakelseNaturalytelseListe" : [ ],
                "pleiepengerPerioder" : [ ]
              } ]
            }
            """;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldDeserializeInntektsmeldingRequest() throws Exception {
        InntektsmeldingRequest result = objectMapper.readValue(INNTEKTSMELDING_JSON, InntektsmeldingRequest.class);

        assertThat(result).isNotNull();
        assertThat(result.getMiljoe()).isEqualTo("q1");
        assertThat(result.getArbeidstakerFnr()).isEqualTo("02889097828");
        assertThat(result.getInntekter()).hasSize(1);
        assertThat(result.getInntekter().get(0).getYtelse()).isEqualTo(YtelseKodeListe.PLEIEPENGER_BARN);
    }

    @Test
    void shouldDeserializeYtelseKodeListeByValue() throws Exception {
        String json = "\"PleiepengerBarn\"";

        YtelseKodeListe result = objectMapper.readValue(json, YtelseKodeListe.class);

        assertThat(result).isEqualTo(YtelseKodeListe.PLEIEPENGER_BARN);
    }

    @Test
    void shouldDeserializeYtelseKodeListeByName() throws Exception {
        String json = "\"PLEIEPENGER_BARN\"";

        YtelseKodeListe result = objectMapper.readValue(json, YtelseKodeListe.class);

        assertThat(result).isEqualTo(YtelseKodeListe.PLEIEPENGER_BARN);
    }

    @Test
    void shouldDeserializeYtelseKodeListeCaseInsensitive() throws Exception {
        String json = "\"pleiepengerbarn\"";

        YtelseKodeListe result = objectMapper.readValue(json, YtelseKodeListe.class);

        assertThat(result).isEqualTo(YtelseKodeListe.PLEIEPENGER_BARN);
    }
}

