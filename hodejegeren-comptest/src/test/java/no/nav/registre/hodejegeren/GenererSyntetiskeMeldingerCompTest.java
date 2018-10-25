package no.nav.registre.hodejegeren;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "classpath:application-test.properties")
public class GenererSyntetiskeMeldingerCompTest {
    
    @ClassRule
    public static WireMockClassRule identpoolStatic = new WireMockClassRule(options().dynamicPort());
    @ClassRule
    public static WireMockClassRule tpsfStatic = new WireMockClassRule(options().dynamicPort());
    @ClassRule
    public static WireMockClassRule tpsSyntStatic = new WireMockClassRule(options().dynamicPort());
    
    @Autowired
    private TpsfConsumer tpsfConsumer;
    @Value("${hodejegeren.ida.credential.username}")
    private String username;
    @Value("${hodejegeren.ida.credential.password}")
    private String password;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        // Set consumed REST-APIs' properties:
        System.setProperty("ident-pool.rest-api.url", "http://localhost:" + identpoolStatic.port() + "/api");
        System.setProperty("tps-forvalteren.rest-api.url", "http://localhost:" + tpsfStatic.port() + "/api");
        System.setProperty("tps-syntetisereren.rest-api.url", "http://localhost:" + tpsSyntStatic.port() + "/api");
        System.setProperty("hodejegeren.ida.credential.username", "ida-username");
        System.setProperty("hodejegeren.ida.credential.password", "ida-password");
    }
    
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
        long gr = 123L;
        tpsfStatic.stubFor(get(urlEqualTo("/api/v1/endringsmelding/skd/identer/123"))
                //                .withQueryParam("aarsakskode",equalTo("[1]"))
                //                .withQueryParam("transaksjonstype",equalTo("1"))
                //                .withBasicAuth(username,password)
                .willReturn(okJson("[\"OKasdf\"]")));
        tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gr, Arrays.asList("2"), "1");
    }
}