package no.nav.registre.inntekt.provider.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsAvsendersystem;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsInntekt;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsInntektsmelding;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsKontaktinformasjon;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsNaturalytelseDetaljer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock
@AutoConfigureMockMvc
public class AltinnInntektIntegrasjonsTest {

    @Value("${path.altinninntekt.satisfactory.json}")
    private String satisfactoryJsonPath;
    @Value("${path.altinninntekt.failing.json}")
    private String failingJsonPath;
    @Value("${path.altininntektconsumer.satisfactory.json}")
    private String satisfactoryAltinnInntektJsonPath;

    @Value("${altinnInntekt.rest.api.url}")
    private String altinnInntektUrl;

    @Autowired(required = false)
    protected WebApplicationContext context;

    @Autowired
    private AltinnInntektController kontroller;

    @Autowired
    private RestTemplate restTemplate;
    /*private MockRestServiceServer mockServer;*/

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String emptyRequestJson;
    private String satisfactoryJson;
    private String satisfactoryAltinnInntektConsumerJson;
    private String failingJson;

    private String loadResourceAsString(String fileName) throws IOException {
        return FileCopyUtils
                .copyToString(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
    }

    @Before
    public void setup() {
        try {
            satisfactoryJson = loadResourceAsString(satisfactoryJsonPath);
            failingJson = loadResourceAsString(failingJsonPath);
            satisfactoryAltinnInntektConsumerJson = loadResourceAsString(satisfactoryAltinnInntektJsonPath);
        } catch (IOException e) {
            System.err.println("Problemer med lasting av testressurser.");
            e.printStackTrace();
        }
        /*mockServer = MockRestServiceServer.createServer(restTemplate);*/
    }

    @After
    public void cleanup(){
        reset();
    }

    @Test
    public void passingCall() throws Exception {
        stubForInntektsmelding();
        stubForAuthorization();
        stubForJournalpost();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/altinnInntekt/enkeltident?includeXml=true")
                .content(satisfactoryJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fnr\":\"12345678910\",\"dokumenter\":[{\"journalpostId\":\"1\",\"dokumentInfoId\":\"2\",\"xml\":\"<dummyXml><title>My Dummy</title><content>This is a dummy xml object.</content></dummyXml>\"}]}"));
    }

    @Test
    public void passingCallNoXml() throws Exception {
        stubForInntektsmelding();
        stubForAuthorization();
        stubForJournalpost();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/altinnInntekt/enkeltident")
                .content(satisfactoryJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fnr\":\"12345678910\",\"dokumenter\":[{\"journalpostId\":\"1\",\"dokumentInfoId\":\"2\"}]}"));
    }

    @Test
    public void noResponseDokmotContinueOnError() throws Exception {
        stubForInntektsmelding();
        stubForAuthorization();
        stubForJournalpost();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/altinnInntekt/enkeltident?continueOnError=true")
                .content(satisfactoryJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fnr\":\"12345678910\",\"dokumenter\":[{\"journalpostId\":\"1\",\"dokumentInfoId\":\"2\"}]}"));
    }

    @Test
    public void noResponseDokmotFailOnError() {

    }

    @Test
    public void responseWhenBadRequest() {

    }

    @Test
    public void validerArbeidsforhold() {

    }

    private void stubForInntektsmelding() {
        stubFor(post(urlEqualTo("/api/v2/inntektsmelding/2018/12/11"))
                 .willReturn(aResponse()
                         .withStatus(200)
                         .withHeader("Content-Type", "text/xml")
                         .withBody("<dummyXml><title>My Dummy</title><content>This is a dummy xml object.</content></dummyXml>")));
    }
    private void stubForAuthorization() {
        stubFor(get(urlEqualTo("/?grant_type=client_credentials&scope=openid"))
                 .withHeader(AUTHORIZATION, containing("Basic "))
                 .willReturn(aResponse()
                         .withStatus(200)));
    }
    private void stubForJournalpost() {
         stubFor(post(urlEqualTo("/rest/journalpostapi/v1/journalpost"))
                 .withHeader(AUTHORIZATION, containing("Bearer "))
                 .willReturn(aResponse()
                         .withStatus(200)
                         .withHeader("Content-Type", "application/json")
                         .withBody("{\"journalpostId\": \"1\",\"journalstatus\":\"Ikke relevant\",\"melding\":\"Dokumentene er postet!\",\"journalpostferdigstilt\":true,\"dokumenter\":[{\"brevkode\":\"TEST BREVKODE\",\"dokumentInfoId\":\"2\",\"tittel\":\"TEST TITTEL\"}]}")));

    }
    private void stubForJournalpostToMeldinger() {
        stubFor(post(urlEqualTo("/rest/journalpostapi/v1/journalpost"))
                 .withHeader(AUTHORIZATION, containing("Bearer "))
                 .willReturn(aResponse()
                         .withStatus(200)
                         .withHeader("Content-Type", "application/json")
                         .withBody("{\"journalpostId\": \"1\",\"journalstatus\":\"Ikke relevant\",\"melding\":\"Dokumentene er postet!\",\"journalpostferdigstilt\":true,\"dokumenter\":[{\"brevkode\":\"TEST BREVKODE\",\"dokumentInfoId\":\"2\",\"tittel\":\"TEST TITTEL\"}, {\"brevkode\":\"Brevkode2\",\"dokumentInfoId\":\"3\",\"tittel\":\"Det andre dokumentet\"}]}")));
    }
    private void stubForJournalpostFeilet() {
        stubFor(post(urlEqualTo("/rest/journalpostapi/v1/journalpost"))
                 .withHeader(AUTHORIZATION, containing("Bearer "))
                 .willReturn(aResponse()
                         .withStatus(200)
                         .withHeader("Content-Type", "application/json")));
    }

    private RsInntektsmelding getPassingMelding() {
        return RsInntektsmelding.builder()
                .ytelse("Opplaeringspenger")
                .aarsakTilInnsending("Ny")
                .arbeidstakerFnr("12345678910")
                .naerRelasjon(false)
                .avsendersystem(RsAvsendersystem.builder()
                        .systemnavn("ORKESTRATOREN")
                        .systemversjon("1")
                        .innsendingstidspunkt(LocalDateTime.parse("2020-06-04T13:10:54.412443")) // OBS! 'get' can be LocalDateTime.now() if not set.
                        .build())
                .arbeidsgiver(RsArbeidsgiver.builder()
                        .virksomhetsnummer("123456789")
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn("GREI BLEIE")
                                .telefonnummer("81549300")
                                .build()).build())
                .arbeidsgiverPrivat(null)
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .arbeidsforholdId("912a41#9$apEA1n")
                        .foersteFravaersdag(null)
                        .beregnetInntekt(RsInntekt.builder()
                                .beloep(50783.0)
                                .aarsakVedEndring(null)
                                .build())
                        .avtaltFerieListe(Collections.emptyList())
                        .utsettelseAvForeldrepengerListe(Collections.emptyList())
                        .graderingIForeldrepengerListe(Collections.emptyList())
                        .build())
                .refusjon(null)
                .omsorgspenger(null)
                .sykepengerIArbeidsgiverperioden(null)
                .startdatoForeldrepengeperiode(null)
                .opphoerAvNaturalytelseListe(Arrays.asList(
                        RsNaturalytelseDetaljer.builder()
                                .naturalytelseType("elektroniskKommunikasjon")
                                .fom(LocalDate.parse("1776-08-09"))
                                .beloepPrMnd(6.02214076)
                                .build(),
                        RsNaturalytelseDetaljer.builder()
                                .naturalytelseType("aksjerGrunnfondsbevisTilUnderkurs")
                                .fom(LocalDate.parse("2020-12-25"))
                                .beloepPrMnd(-3.14159265359)
                                .build()))
                .gjenopptakelseNaturalytelseListe(Collections.emptyList())
                .pleiepengerPerioder(Collections.emptyList())
                .build();
    }

}
