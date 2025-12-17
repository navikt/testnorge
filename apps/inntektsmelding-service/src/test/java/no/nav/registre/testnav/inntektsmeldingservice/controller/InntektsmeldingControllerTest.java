package no.nav.registre.testnav.inntektsmeldingservice.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.testnav.inntektsmeldingservice.TestSecurityConfig;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.DokmotConsumer;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.GenererInntektsmeldingConsumer;
import no.nav.registre.testnav.inntektsmeldingservice.repository.InntektsmeldingRepository;
import no.nav.registre.testnav.inntektsmeldingservice.repository.model.InntektsmeldingModel;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.response.InntektsmeldingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@Slf4j
@Import(TestSecurityConfig.class)
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
    private WebTestClient webTestClient;

    @MockitoBean
    private GenererInntektsmeldingConsumer genererInntektsmeldingConsumer;

    @MockitoBean
    private DokmotConsumer dokmotConsumer;

    @MockitoBean
    private InntektsmeldingRepository inntektsmeldingRepository;

    @BeforeEach
    void beforeEach() {
        when(genererInntektsmeldingConsumer.getInntektsmeldingXml201812(any())).thenReturn("xml");
        when(dokmotConsumer.opprettJournalpost(any(), any(), any())).thenReturn(Collections.emptyList());
        when(inntektsmeldingRepository.save(any())).thenReturn(InntektsmeldingModel.builder().id(1L).build());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2"})
    void genererMeldingForIdentTest(String env) {
        var json = JSON.replace("%ENV%", env);
        
        webTestClient
                .post()
                .uri("/api/v1/inntektsmelding")
                .header("Nav-Call-Id", NAV_CALL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InntektsmeldingResponse.class)
                .value(response -> {
                    log.info("Response: {}", response);
                    assertThat(response).isNotNull();
                    assertThat(response.getFnr()).isEqualTo("02889097828");
                });
        
        verify(genererInntektsmeldingConsumer).getInntektsmeldingXml201812(any());
        verify(dokmotConsumer).opprettJournalpost(eq(env), any(), eq(NAV_CALL_ID));

    }

}
