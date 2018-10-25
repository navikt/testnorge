package no.nav.registre.hodejegeren;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.*;

import java.io.File;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
public class GenererSyntetiskeMeldingerCompTest {
    
    @ClassRule
    public static WireMockClassRule identpoolStatic = new WireMockClassRule(options().dynamicPort());
    @ClassRule
    public static WireMockClassRule tpsfStatic = new WireMockClassRule(options().dynamicPort());
    @ClassRule
    public static WireMockClassRule tpsSyntStatic = new WireMockClassRule(options().dynamicPort());
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        // Set consumed REST-APIs' properties:
        System.setProperty("ident-pool.rest-api.url", "http://localhost:" + identpoolStatic.port() + "/api");
        System.setProperty("tps-forvalteren.rest-api.url", "http://localhost:" + tpsfStatic.port() + "/api");
        System.setProperty("tps-syntetisereren.rest-api.url", "http://localhost:" + tpsSyntStatic.port() + "/api");
    }
    
    /**
     * Komponenttest av følgende scenario: Happypath - test
     * (se løsningsbeskrivelse for hele prosedyren som testes i happypath: https://confluence.adeo.no/display/FEL/TPSF+Hodejegeren)
     * HVIS endepunktet kalles med bestilling av et gitt antall syntetiserte skdmeldinger for et utvalg av årsakskoder, Så skal
     * disse meldingene lagres i TPSF på endepunktet /v1/endringsmelding/skd/save/{gruppeId}, og id-ene til meldingene returneres.
     * Meldingene skal bestå av gyldige identer (FNR/DNR) i tråd med meldingens felter og som være identer som stemmer overens med TPS
     * i det miljøet som angis av bestillingen/request.
     * Tilhørende felter må stemme overens med status quo i TPS på de eksisterende identer som mates inn i
     * de syntetiserte meldingene som hodejegeren henter fra TPS Syntetisereren.
     */
    @Test
    public void shouldGenerereSyntetiserteMeldinger() {
    
    }
}