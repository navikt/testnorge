package no.nav.registre.hodejegeren.comptests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.common.io.Resources;

import no.nav.registre.hodejegeren.provider.rs.TriggeSyntetiseringController;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class GenererSyntetiskeMeldingerCompTest {
    
    private List<String> expectedMeldingsIdsITpsf = Arrays.asList("120421016", "110156008");
    private List<String> expectedFnrFromIdentpool = Arrays.asList("11111111111", "22222222222");
    private long gruppeId = 123L;
    private Integer antallMeldinger = 2;
    private String aarsakskodeFoedselsmelding = "0210";
    
    @Autowired
    private TriggeSyntetiseringController triggeSyntetiseringController;
    @Value("${hodejegeren.ida.credential.username}")
    private String username;
    @Value("${hodejegeren.ida.credential.password}")
    private String password;
    
    /**
     * Komponenttest av følgende scenario: Happypath - test
     * Hodejegeren henter syntetiserte skdmelidnger fra TPS Syntetisereren, mater dem med identer og tilhørende info, og lagrer meldingene i TPSF.
     * (se løsningsbeskrivelse for hele prosedyren som testes i happypath: https://confluence.adeo.no/display/FEL/TPSF+Hodejegeren)
     * <p>
     * HVIS endepunktet kalles med bestilling av et gitt antall syntetiserte skdmeldinger for et utvalg av årsakskoder, Så skal
     * disse meldingene lagres i TPSF på endepunktet /v1/endringsmelding/skd/save/{gruppeId}, og id-ene til meldingene returneres.
     * Meldingene skal bestå av gyldige identer (FNR/DNR) i tråd med meldingens felter og som være identer som stemmer overens med TPS
     * i det miljøet som angis av bestillingen/request.
     * Tilhørende felter må stemme overens med status quo i TPS på de eksisterende identer som mates inn i
     * de syntetiserte meldingene.
     */
    @Test
    public void shouldGenerereSyntetiserteMeldinger() {
        HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put(aarsakskodeFoedselsmelding, antallMeldinger);
    
        stubTPSF(gruppeId);
        stubTpsSynt();
        stubIdentpool();
        
        GenereringsOrdreRequest ordreRequest = new GenereringsOrdreRequest(gruppeId, "t10", antallMeldingerPerAarsakskode);
        List<Long> meldingsIderITpsf = triggeSyntetiseringController.genererSyntetiskeMeldingerOgLagreITpsf(ordreRequest);
    
        assertEquals(expectedMeldingsIdsITpsf, meldingsIderITpsf);
    }
    
    private void stubIdentpool() {
        stubFor(post("/identpool/api/v1/identifikator")
                .withRequestBody(equalToJson(getResourceFileContent("__files/comptest/identpool/identpool_hent2Identer_request.json")))
                .willReturn(okJson(expectedFnrFromIdentpool.toString())));
    }
    
    private void stubTpsSynt() {
        stubFor(get(urlEqualTo("/api/generate"))
                .withQueryParam("aarsakskode", equalTo(aarsakskodeFoedselsmelding))
                .withQueryParam("antallMeldinger", equalTo(antallMeldinger.toString()))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBodyFile("comptest/tpssynt/tpsSynt_aarsakskode02_2meldinger_Response.json")));
    }
    
    private void stubTPSF(long gruppeId) {
        stubFor(get(urlEqualTo("/tpsf/api/v1/endringsmelding/skd/identer/" + gruppeId))
                //   .withQueryParam("aarsakskode",containing("1")) //getForObject sine urivariables blir visst ikke registrert i wiremock, så derfor fungerer ikke withQueryParam-sjekken.
                //   .withQueryParam("transaksjonstype",equalTo("1"))
                .withBasicAuth(username, password)
                .willReturn(okJson("[\n"
                        + "  \"12042101557\",\n"
                        + "  \"01015600248\"\n"
                        + "]")));
    
        stubFor(post("/tpsf/api/save/" + gruppeId)
                .withRequestBody(equalToJson(getResourceFileContent("__files/comptest/tpsf/tpsf_save_aarsakskode02_2ferdigeMeldinger_request.json")))
                .willReturn(okJson(expectedMeldingsIdsITpsf.toString())));
    }
    
    private String getResourceFileContent(String path) {
        URL fileUrl = Resources.getResource(path);
        try {
            return Resources.toString(fileUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}