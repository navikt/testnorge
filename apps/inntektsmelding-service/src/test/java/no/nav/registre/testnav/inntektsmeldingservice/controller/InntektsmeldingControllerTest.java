package no.nav.registre.testnav.inntektsmeldingservice.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.DokmotConsumer;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.GenererInntektsmeldingConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class InntektsmeldingControllerTest {

    private static final String JSON = """
            {
              "miljoe" : "%ENV%",
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
    private static final String RESULT = """
            {
              "fnr" : "02889097828",
              "dokumenter" : [ ]
            }
            """;
    private static final String NAV_CALL_ID = "navCallId";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenererInntektsmeldingConsumer genererInntektsmeldingConsumer;

    @MockitoBean
    private DokmotConsumer dokmotConsumer;

    @BeforeEach
    void beforeEach() {
        when(genererInntektsmeldingConsumer.getInntektsmeldingXml201812(any())).thenReturn("xml");
        when(dokmotConsumer.opprettJournalpost(any(), any(), any())).thenReturn(Collections.emptyList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2"})
    void genererMeldingForIdentTest(String env)
            throws Exception {

        var json = JSON.replace("%ENV%", env);
        var result = mockMvc
                .perform(
                        post("/api/v1/inntektsmelding")
                                .header("Nav-Call-Id", NAV_CALL_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(genererInntektsmeldingConsumer).getInntektsmeldingXml201812(any());
        verify(dokmotConsumer).opprettJournalpost(eq(env), any(), eq(NAV_CALL_ID));
        assertThat(result).isEqualToIgnoringWhitespace(RESULT);

    }

}
