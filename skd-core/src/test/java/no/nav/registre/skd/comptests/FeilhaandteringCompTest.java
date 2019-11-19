package no.nav.registre.skd.comptests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static javax.security.auth.callback.ConfirmationCallback.OK;
import static no.nav.registre.skd.service.Endringskoder.INNVANDRING;
import static no.nav.registre.skd.service.Endringskoder.NAVNEENDRING_FOERSTE;
import static no.nav.registre.skd.testutils.ResourceUtils.getResourceFileContent;
import static no.nav.registre.skd.testutils.StrSubstitutor.replace;
import static no.nav.registre.skd.testutils.Utils.testLoggingInClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import no.nav.registre.skd.ApplicationStarter;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.provider.rs.SyntetiseringController;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.skd.service.SyntetiseringService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApplicationTestConfig.class, ApplicationStarter.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class FeilhaandteringCompTest {

    private String testfeilmelding = "testfeilmelding";
    private List<String> expectedFnrFromIdentpool = Arrays.asList("11111111111", "22222222222");
    private List<Long> expectedMeldingsIdsITpsf = Arrays.asList(120421016L, 110156008L);
    private Integer antallMeldinger = 2;
    private String miljoe = "t10";

    @Autowired
    private SyntetiseringController syntetiseringController;

    @Autowired
    private Random randMock;

    @Value("${testnorges.ida.credential.tpsf.username}")
    private String username;

    @Value("${testnorges.ida.credential.tpsf.password}")
    private String password;

    /**
     * Hvis en Exception blir kastet som det ikke er spesiell behandling for, SÅ skal de database-idene til de skdmeldingene som
     * allerede er persistert i TPSF, bli loggført før feilmeldingen kastes videre.
     * <p>
     * Feilmeldingen som kastes i denne feilhåndteringstesten, er HttpServerErrorException fra et rest-kall mot TPSF service
     * routine.
     * <p>
     * Her vil første bolk med innvandringsmelding suksessfult lagres. Deretter vil det kastes feilmelding når eksisterende identer
     * hentes fra TPS via TPSF for å behandle NAVNEENDRING_FOERSTE -meldingene.
     */
    @Test
    public void generellFeilhaandtering() {
        when(randMock.nextInt(anyInt())).thenReturn(0);
        ListAppender<ILoggingEvent> listAppender = testLoggingInClass(SyntetiseringService.class);
        HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put(INNVANDRING.getEndringskode(), antallMeldinger);
        antallMeldingerPerAarsakskode.put(NAVNEENDRING_FOERSTE.getEndringskode(), antallMeldinger);
        var gruppeId = 123L;

        stubIdentpool();
        stubHodejegeren(gruppeId);
        stubTpsf(gruppeId);
        stubSendIderTilTps(gruppeId);
        stubTpConsumer();

        stubTpsSynt(INNVANDRING.getEndringskode(), antallMeldinger, "comptest/tpssynt/tpsSynt_aarsakskode02_2meldinger_Response.json");
        stubTpsSynt(NAVNEENDRING_FOERSTE.getEndringskode(), antallMeldinger, "comptest/tpssynt/tpsSynt_aarsakskode06_2meldinger_Response.json");

        var ordreRequest = new GenereringsOrdreRequest(gruppeId, miljoe, antallMeldingerPerAarsakskode);
        var response = syntetiseringController.genererSkdMeldinger(ordreRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(5, listAppender.list.size());
        var skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();
        assertThat(skdMeldingerTilTpsRespons.getAntallSendte(), is(equalTo(2)));
        assertThat(listAppender.list.toString(), containsString("Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: []"));
    }

    private void stubIdentpool() {
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        HashMap<String, String> placeholderValues = new HashMap<>();
        placeholderValues.put("foedtEtter", LocalDate.now().minusYears(90).format(formatter));
        placeholderValues.put("foedtFoer", LocalDate.now().format(formatter));

        var path = "__files/comptest/identpool/identpool_hent2Identer_request.json";
        stubFor(post("/identpool/api/v1/identifikator?finnNaermesteLedigeDato=false")
                .withRequestBody(equalToJson(replace(getResourceFileContent(path), placeholderValues)))
                .willReturn(okJson(expectedFnrFromIdentpool.toString())));

        stubFor(get("/identpool/api/v1/fiktive-navn/tilfeldig?antall=1").willReturn(okJson("{\"fornavn\": \"A\", \"etternavn\": \"B\"}")));
    }

    private void stubTpsSynt(String endringskode, Integer antallMeldinger, String responseBodyFile) {
        stubFor(get(urlEqualTo("/tpssynt/api/v1/generate/tps/" + endringskode + "?numToGenerate=" + antallMeldinger))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBodyFile(responseBodyFile)));
    }

    private void stubHodejegeren(long gruppeId) {
        var fnrs = new ArrayList<>(Arrays.asList("01010101010", "02020202020", "33333333333", "44444444444", "55555555555", "66666666666"));
        // Henter alle identer i avspillergruppa hos TPSF:
        stubHodejegerenHentLevendeIdenter(gruppeId,
                "["
                        + "\"" + fnrs.get(0) + "\",\n"
                        + "\"" + fnrs.get(1) + "\",\n"
                        + "\"" + fnrs.get(2) + "\",\n"
                        + "\"" + fnrs.get(3) + "\",\n"
                        + "\"" + fnrs.get(4) + "\",\n"
                        + "\"" + fnrs.get(5) + "\"]");

        // Henter liste med alle døde eller utvandrede identer i avspillergruppa hos TPSF:
        stubHodejegerenHentDoedeIdenter(gruppeId, "[\n  \"" + fnrs.get(2) + "\",\n  \"" + fnrs.get(3) + "\"\n]");

        // Henter liste over alle gifte identer i avspillergruppa hos TPSF:
        stubHodejegerenHentGifteIdenter(gruppeId, "[\n\"" + fnrs.get(4) + "\",\n\"" + fnrs.get(5) + "\"\n]");

        for (var fnr : fnrs) {
            stubHodejegerenHentStatusQuo(fnr, NAVNEENDRING_FOERSTE.getEndringskode());
        }
    }

    private void stubTpsf(long gruppeId) {
        // Lagrer meldingene og får liste over database-id-ene til de lagrede meldingene i retur.
        stubFor(post("/tpsf/api/v1/endringsmelding/skd/save/" + gruppeId)
                .withRequestBody(equalToJson(getResourceFileContent("__files/comptest/tpsf/tpsf_save_aarsakskode02_2ferdigeMeldinger_request.json")))
                .willReturn(okJson(expectedMeldingsIdsITpsf.toString())));

        var expectedAntallFeilet = 0;

        // Sender skdmeldingene til TPS
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/send/" + gruppeId))
                .withRequestBody(equalToJson(
                        "{\"environment\":\"" + miljoe
                                + "\",\"ids\":[" + expectedMeldingsIdsITpsf.get(0) + ", " + expectedMeldingsIdsITpsf.get(1) + "]}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedMeldingsIdsITpsf.size()
                                + "\", \"antallFeilet\": \"" + expectedAntallFeilet
                                + "\", \"statusFraFeilendeMeldinger\": ["
                                + "{\"foedselsnummer\": \"" + expectedFnrFromIdentpool.get(0)
                                + "\", \"sekvensnummer\": \"\""
                                + ", \"status\": \"\"},"
                                + "{\"foedselsnummer\": \"" + expectedFnrFromIdentpool.get(1)
                                + "\", \"sekvensnummer\": \"\""
                                + ", \"status\": \"\"}],"
                                + "\"tpsfIds\": [" + expectedMeldingsIdsITpsf.get(0) + ", " + expectedMeldingsIdsITpsf.get(1) + "]}")));
    }

    private void stubHodejegerenHentLevendeIdenter(long gruppeId, String okJsonResponse) {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/alle-levende-identer/" + gruppeId))
                .willReturn(okJson(okJsonResponse)));
    }

    private void stubHodejegerenHentDoedeIdenter(long gruppeId, String okJsonResponse) {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/doede-identer/" + gruppeId))
                .willReturn(okJson(okJsonResponse)));
    }

    private void stubHodejegerenHentGifteIdenter(long gruppeId, String okJsonResponse) {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/gifte-identer/" + gruppeId))
                .willReturn(okJson(okJsonResponse)));
    }

    private void stubHodejegerenHentStatusQuo(String fnr, String endringskode) {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/status-quo?miljoe=" + miljoe + "&fnr=" + fnr + "&endringskode=" + endringskode))
                .willReturn(aResponse().withStatus(500).withBody("{\"message\":\"" + testfeilmelding + "\"}")));
    }

    private void stubSendIderTilTps(long gruppeId) {
        stubFor(get(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/send/" + gruppeId))
                .willReturn(aResponse().withStatus(OK)));
    }

    private void stubTpConsumer() {
        stubFor(post(urlPathEqualTo("/tp/api/v1/orkestrering/opprettPersoner/" + miljoe))
                .withRequestBody(equalToJson(
                        "[\"" + expectedFnrFromIdentpool.get(0) + "\", \"" + expectedFnrFromIdentpool.get(1) + "\"]"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[\"" + expectedFnrFromIdentpool.get(1) + "\"]")));
    }
}