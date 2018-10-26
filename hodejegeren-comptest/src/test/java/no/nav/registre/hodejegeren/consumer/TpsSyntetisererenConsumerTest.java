package no.nav.registre.hodejegeren.consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import no.nav.registre.hodejegeren.ApplicationTestBase;
import no.nav.registre.hodejegeren.AssertionUtils;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

public class TpsSyntetisererenConsumerTest extends ApplicationTestBase {
    
    @Autowired
    TpsSyntetisererenConsumer tpsSyntetisererenConsumer;
    
    /**
     * Tester at alle navnene i forventet responsmelding fra TPS Syntetisereren
     * blir med i java-objektet RsMeldingstype1Felter.
     */
    @Test
    public void shouldDeserialiseAllFieldsInTheResponse() throws InvocationTargetException, IllegalAccessException {
        tpsSyntStatic.stubFor(get(urlEqualTo("/api/generate")).willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("tpssynt/tpsSynt_NotNullFields_Response.json")));
        
        List<RsMeldingstype> skdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("aa", 1);
        
        List<String> ignoredFields = Arrays.asList("getSaksid", "getEmbete", "getSakstype",
                "getVedtaksdato", "getInternVergeid", "getVergeFnrDnr", "getVergetype",
                "getMandattype", "getMandatTekst", "getReserverFramtidigBruk");
        AssertionUtils.assertAllFieldsNotNull(((RsMeldingstype1Felter) skdmeldinger.get(0)), ignoredFields);
    }
}