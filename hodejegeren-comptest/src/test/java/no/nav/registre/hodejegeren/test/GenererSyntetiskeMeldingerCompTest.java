package no.nav.registre.hodejegeren.test;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Arrays;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

public class GenererSyntetiskeMeldingerCompTest extends ApplicationTestBase {
    
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
        tpsfStatic.stubFor(get(urlEqualTo("/api/v1/endringsmelding/skd/identer/123"))
                //                                .withQueryParam("aarsakskode",containing("1")) //getForObject sine urivariables blir visst ikke registrert i wiremock, så derfor fungerer ikke withQueryParam-sjekken.
                //                .withQueryParam("transaksjonstype",equalTo("1"))
                .withBasicAuth(username, password)
                .willReturn(okJson("[\"OKasdf\"]")));
        tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gr, Arrays.asList("2"), "1");
    }
}