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
import static no.nav.registre.skd.testutils.ResourceUtils.getResourceFileContent;
import static no.nav.registre.skd.testutils.StrSubstitutor.replace;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import no.nav.registre.skd.ApplicationStarter;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.provider.rs.SyntetiseringController;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApplicationTestConfig.class, ApplicationStarter.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class GenererSyntetiskeMeldingerCompTest {

    private List<Long> expectedMeldingsIdsITpsf = new ArrayList<>();
    private List<String> expectedFnrFromIdentpool = Arrays.asList("11111111111", "22222222222");
    private long gruppeId = 123L;
    private String miljoe = "t10";
    private Integer antallMeldinger = 2;
    private String endringskodeInnvandringsmelding = "0211";

    @Autowired
    private SyntetiseringController syntetiseringController;

    @Value("${testnorges.ida.credential.tpsf.username}")
    private String username;

    @Value("${testnorges.ida.credential.tpsf.password}")
    private String password;

    @Before
    public void setUp() {
        expectedMeldingsIdsITpsf.addAll(Arrays.asList(120421016L, 110156008L));
    }

    /**
     * Komponenttest av følgende scenario: Happypath-test med 2 innvandringsmeldinger.
     * <p>
     * <p>
     * Testnorge-skd henter syntetiserte skdmeldinger fra TPS Syntetisereren, mater dem med identer og tilhørende info, og lagrer
     * meldingene i TPSF.
     * <p>
     * HVIS endepunktet kalles med bestilling av et gitt antall syntetiserte skdmeldinger for et utvalg av årsakskoder, Så skal
     * disse meldingene lagres i TPSF på endepunktet /v1/endringsmelding/skd/save/{skdMeldingGruppeId}, og id-ene til meldingene returneres.
     * Meldingene skal bestå av gyldige identer (FNR/DNR) i tråd med meldingens felter og som være identer som stemmer overens med
     * TPS i det miljøet som angis av bestillingen/request. Tilhørende felter må stemme overens med status quo i TPS på de
     * eksisterende identer som mates inn i de syntetiserte meldingene.
     * <p>
     * Testen tester f.o.m. RestController t.o.m. Consumer-klassene. Wiremock brukes.
     */
    @Test
    public void shouldGenerereSyntetiserteMeldinger() {
        HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put(endringskodeInnvandringsmelding, antallMeldinger);

        expectedMeldingsIdsITpsf.addAll(Arrays.asList(120421017L, 110156009L));

        stubHodejegeren(gruppeId);
        stubTpsSynt();
        stubIdentpool();
        stubTpsf(gruppeId);
        stubTp();

        GenereringsOrdreRequest ordreRequest = new GenereringsOrdreRequest(gruppeId, miljoe, antallMeldingerPerAarsakskode);

        SkdMeldingerTilTpsRespons respons = (SkdMeldingerTilTpsRespons) syntetiseringController.genererSkdMeldinger(ordreRequest).getBody();

        assert respons != null;
        assertEquals(4, expectedMeldingsIdsITpsf.size());
        assertEquals(4, respons.getAntallSendte());
        assertEquals(expectedFnrFromIdentpool.get(0), respons.getStatusFraFeilendeMeldinger().get(0).getFoedselsnummer());
        assertEquals(expectedFnrFromIdentpool.get(1), respons.getStatusFraFeilendeMeldinger().get(1).getFoedselsnummer());
    }

    private void stubIdentpool() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        HashMap<String, String> placeholderValues = new HashMap<>();
        placeholderValues.put("foedtEtter", LocalDate.now().minusYears(90).format(formatter));
        placeholderValues.put("foedtFoer", LocalDate.now().format(formatter));

        String path = "__files/comptest/identpool/identpool_hent2Identer_request.json";
        stubFor(post("/identpool/api/v1/identifikator?finnNaermesteLedigeDato=false")
                .withRequestBody(equalToJson(replace(getResourceFileContent(path), placeholderValues)))
                .willReturn(okJson(expectedFnrFromIdentpool.toString())));
    }

    private void stubTpsSynt() {
        stubFor(get(urlEqualTo("/tpssynt/api/v1/generate/tps/" + endringskodeInnvandringsmelding + "?numToGenerate=" + antallMeldinger))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBodyFile("comptest/tpssynt/tpsSynt_aarsakskode02_2meldinger_Response.json")));
    }

    private void stubHodejegeren(long gruppeId) {
        // Hodejegeren henter alle identer i avspillergruppa hos TPSF:
        stubHodejegerenHentLevendeIdenter(gruppeId, "[\"12042101557\",\n\"01015600248\"\n]");

        // Hodejegeren henter liste med alle døde eller utvandrede identer i avspillergruppa hos TPSF:
        stubHodejegerenHentDoedeIdenter(gruppeId, "[\n  \"44444444444\",\n  \"55555555555\"\n]");

        // Hodejegeren henter liste over alle gifte identer i avspillergruppa hos TPSF:
        stubHodejegerenHentGifteIdenter(gruppeId, "[]");
    }

    private void stubTpsf(long gruppeId) {
        // Lagrer meldingene og får liste over database-id-ene til de lagrede meldingene i retur.
        stubFor(post("/tpsf/api/v1/endringsmelding/skd/save/" + gruppeId)
                .withRequestBody(equalToJson(getResourceFileContent("__files/comptest/tpsf/tpsf_save_aarsakskode02_2ferdigeMeldinger_request.json")))
                .willReturn(okJson(expectedMeldingsIdsITpsf.toString())));

        // Sender meldingene til TPS
        stubFor(post("/tpsf/api/v1/endringsmelding/skd/send/" + gruppeId)
                .withRequestBody(equalToJson("{\"environment\": \"" + miljoe + "\", \"ids\": [120421016, 110156008, 120421017, 110156009]}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedMeldingsIdsITpsf.size()
                                + "\", \"antallFeilet\": \"" + 0
                                + "\", \"statusFraFeilendeMeldinger\": ["
                                + "{\"foedselsnummer\": \"" + expectedFnrFromIdentpool.get(0)
                                + "\", \"sekvensnummer\": \"\""
                                + ", \"status\": \"\"},"
                                + "{\"foedselsnummer\": \"" + expectedFnrFromIdentpool.get(1)
                                + "\", \"sekvensnummer\": \"\""
                                + ", \"status\": \"\"}]}")));
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

    private void stubTp() {
        stubFor(post(urlPathEqualTo("/tp/api/v1/orkestrering/opprettPersoner/" + miljoe))
                .willReturn(okJson("[]")));
    }
}