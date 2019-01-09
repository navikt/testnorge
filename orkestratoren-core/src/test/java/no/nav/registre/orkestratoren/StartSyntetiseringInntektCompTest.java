package no.nav.registre.orkestratoren;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.provider.rs.SyntetiseringsController;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class StartSyntetiseringInntektCompTest {

    String expectedFnrMedInntektsmelding = "11110061111";
    private Long avspillergruppeId = 123L;

    @Autowired
    private SyntetiseringsController syntetiseringsController;
    @Value("${testnorges.ida.credential.tpsf.username}")
    private String username;
    @Value("${testnorges.ida.credential.tpsf.password}")
    private String password;

    /**
     * Orkestratoren forventer å få fødselsnumrene i samme rekkefølge fra TPSF avspillergruppen hver gang.
     * Formålet er at de alle fødselsnumre som tidligere har blitt sendt til inntekt-synt,
     * skal sendes dit igjen for å få månedens inntektsmelding.
     * I tillegg kommer nye fødselsnumre ettersom listen med fødselsnumre i TPSF avspillergruppen forventes å øke.
     * <p>
     * Test-scenario: Første halvdel av fødselsnummer-listen fra TPSF avspillergruppen blir sendt til
     * Inntekt-synt "generer syntetisk inntektsmelding"-endepunktet.
     */
    @Test

    public void shouldBestilleinntektsmeldingForFoersteHalvpartAvFnrFraTpsf() {
        stubTPSF();
        stubInntektSynt();

        List<String> fnr = syntetiseringsController.opprettSyntetiskInntektsmeldingIInntektstub(new SyntetiserInntektsmeldingRequest(avspillergruppeId));
        assertEquals(Arrays.asList(expectedFnrMedInntektsmelding), fnr);
    }

    private void stubInntektSynt() {
        stubFor(post("/inntektsynt/api/v1/generate")
                .withRequestBody(equalToJson("[\"" + expectedFnrMedInntektsmelding + "\"]"))
                .willReturn(created()));
    }

    public void stubTPSF() {
        //Hodejegeren henter fnr i avspillergruppa hos TPSF:
        stubTpsfFiltrerIdenterPaaAarsakskode(avspillergruppeId, "01,02,39", "[\"" + expectedFnrMedInntektsmelding + "\",\n\"22222222222\"\n,\n\"33333333333\"\n]");
        stubTpsfFiltrerIdenterPaaAarsakskode(avspillergruppeId, "43,32", "[\n\"33333333333\"\n]"); //død eller utvandret
    }

    private void stubTpsfFiltrerIdenterPaaAarsakskode(long gruppeId, String aarsakskode, String okJsonResponse) {
        stubFor(get(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/identer/" + gruppeId))
                .withQueryParam("aarsakskode", equalTo(aarsakskode))
                .withQueryParam("transaksjonstype", equalTo("1"))
                .withBasicAuth(username, password)
                .willReturn(okJson(okJsonResponse)));
    }
}
