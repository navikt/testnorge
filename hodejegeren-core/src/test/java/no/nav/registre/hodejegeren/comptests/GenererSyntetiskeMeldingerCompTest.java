package no.nav.registre.hodejegeren.comptests;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class GenererSyntetiskeMeldingerCompTest {
    
    @Autowired
    private TpsfConsumer tpsfConsumer;
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
        long gr = 123L;
        stubFor(get(urlEqualTo("/tpsf/api/v1/endringsmelding/skd/identer/123"))
                //                                .withQueryParam("aarsakskode",containing("1")) //getForObject sine urivariables blir visst ikke registrert i wiremock, så derfor fungerer ikke withQueryParam-sjekken.
                //                .withQueryParam("transaksjonstype",equalTo("1"))
                .withBasicAuth(username, password)
                .willReturn(okJson("[\"OKasdf\"]")));
        tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gr, Arrays.asList("2"), "1");
    }
}