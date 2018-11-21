package no.nav.registre.hodejegeren.comptests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.NAVNEENDRING_FOERSTE;
import static no.nav.registre.hodejegeren.service.Endringskoder.VIGSEL;
import static no.nav.registre.hodejegeren.testutils.ResourceUtils.getResourceFileContent;
import static no.nav.registre.hodejegeren.testutils.Utils.testLoggingInClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.hodejegeren.ApplicationStarter;
import no.nav.registre.hodejegeren.exception.IkkeFullfoertBehandlingException;
import no.nav.registre.hodejegeren.provider.rs.TriggeSyntetiseringController;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.service.HodejegerService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApplicationTestConfig.class, ApplicationStarter.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class FeilhaandteringCompTest {

    String testfeilmelding = "testfeilmelding";
    private List<String> expectedFnrFromIdentpool = Arrays.asList("11111111111", "22222222222");
    private List<Long> expectedMeldingsIdsITpsf = Arrays.asList(120421016L, 110156008L);
    private Integer antallMeldinger = 2;
    private String t10 = "t10";
    @Autowired
    private TriggeSyntetiseringController triggeSyntetiseringController;
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
    public void generellFeilhaandtering() throws JsonProcessingException {
        when(randMock.nextInt(anyInt())).thenReturn(0);
        ListAppender<ILoggingEvent> listAppender = testLoggingInClass(HodejegerService.class);
        HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put(INNVANDRING.getEndringskode(), antallMeldinger);
        antallMeldingerPerAarsakskode.put(NAVNEENDRING_FOERSTE.getEndringskode(), antallMeldinger);
        long gruppeId = 123L;

        stubIdentpool();
        stubTPSF(gruppeId);
        stubTpsSynt(INNVANDRING.getEndringskode(), antallMeldinger, "comptest/tpssynt/tpsSynt_aarsakskode02_2meldinger_Response.json");
        stubTpsSynt(NAVNEENDRING_FOERSTE.getEndringskode(), antallMeldinger, "comptest/tpssynt/tpsSynt_aarsakskode06_2meldinger_Response.json");
        try {
            GenereringsOrdreRequest ordreRequest = new GenereringsOrdreRequest(gruppeId, t10, antallMeldingerPerAarsakskode);
            triggeSyntetiseringController.genererSyntetiskeMeldingerOgLagreITpsf(ordreRequest);
            fail();
        } catch (IkkeFullfoertBehandlingException e) {
            assertEquals(3, listAppender.list.size());
            assertTrue(listAppender.list.toString()
                    .contains("Skdmeldinger som var ferdig behandlet før noe feilet, har følgende id-er i TPSF: " + expectedMeldingsIdsITpsf.toString()));
            assertEquals(expectedMeldingsIdsITpsf, e.getIds());
        }
    }

    private void stubIdentpool() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        stubFor(post("/identpool/api/v1/identifikator")
                .withRequestBody(equalToJson("{\n" +
                        "  \"identtype\": \"FNR\",\n" +
                        "  \"foedtEtter\": \"" + LocalDate.now().minusYears(90).format(formatter) + "\",\n" +
                        "  \"foedtFoer\": null,\n" +
                        "  \"kjoenn\": null,\n" +
                        "  \"antall\": 2\n" +
                        "}"))
                .willReturn(okJson(expectedFnrFromIdentpool.toString())));
    }

    private void stubTpsSynt(String endringskode, Integer antallMeldinger, String responseBodyFile) {
        stubFor(get(urlPathEqualTo("/tpssynt/api/generate"))
                .withQueryParam("endringskode", equalTo(endringskode))
                .withQueryParam("antallMeldinger", equalTo(antallMeldinger.toString()))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBodyFile(responseBodyFile)));
    }

    private void stubTPSF(long gruppeId) {
        // Hodejegeren henter alle identer i avspillergruppa hos TPSF:
        stubTpsfFiltrerIdenterPaaAarsakskode(gruppeId, "01,02,39,91",
                "[\"01010101010\",\n\"02020202020\"\n,\"33333333333\",\n  \"44444444444\"\n,\n\"55555555555\",\n\"66666666666\"\n]");

        // Hodejegeren henter liste med alle døde eller utvandrede identer i avspillergruppa hos TPSF:
        stubTpsfFiltrerIdenterPaaAarsakskode(gruppeId, "43,32", "[\n  \"33333333333\",\n  \"44444444444\"\n]");

        // Hodejegeren henter liste over alle gifte identer i avspillergruppa hos TPSF:
        stubTpsfFiltrerIdenterPaaAarsakskode(gruppeId, VIGSEL.getAarsakskode(), "[\n\"55555555555\",\n\"66666666666\"\n]");

        stubFor(get(urlPathEqualTo("/tpsf/api/v1/serviceroutine/FS03-FDNUMMER-PERSDATA-O"))
                .withQueryParam("aksjonsKode", equalTo("A0"))
                .withQueryParam("environment", equalTo(t10))
                .withQueryParam("fnr", equalTo("55555555555"))
                .withBasicAuth(username, password)
                .willReturn(aResponse().withStatus(500).withBody("{\"message\":\"" + testfeilmelding + "\"}")));

        // Hodejegeren lagrer meldingene og får liste over database-id-ene til de lagrede meldingene i retur.
        stubFor(post("/tpsf/api/v1/endringsmelding/skd/save/" + gruppeId)
                .withRequestBody(equalToJson(getResourceFileContent("__files/comptest/tpsf/tpsf_save_aarsakskode02_2ferdigeMeldinger_request.json")))
                .withBasicAuth(username, password)
                .willReturn(okJson(expectedMeldingsIdsITpsf.toString())));
    }

    private void stubTpsfFiltrerIdenterPaaAarsakskode(long gruppeId, String aarsakskode, String okJsonResponse) {
        stubFor(get(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/identer/" + gruppeId))
                .withQueryParam("aarsakskode", equalTo(aarsakskode))
                .withQueryParam("transaksjonstype", equalTo("1"))
                .withBasicAuth(username, password)
                .willReturn(okJson(okJsonResponse)));
    }
}