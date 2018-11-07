package no.nav.registre.hodejegeren.consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.hodejegeren.comptests.AssertionUtils;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "tps-syntetisereren.rest-api.url=http://localhost:${wiremock.server.port}/api",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class TpsSyntetisererenConsumerITest {
    
    @Autowired
    TpsSyntetisererenConsumer tpsSyntetisererenConsumer;
    
    /**
     * Tester at alle navnene i forventet responsmelding fra TPS Syntetisereren
     * blir med i java-objektet RsMeldingstype1Felter.
     */
    @Test
    public void shouldDeserialiseAllFieldsInTheResponse() throws InvocationTargetException, IllegalAccessException {
        stubFor(get(urlEqualTo("/api/generate?endringskode=aa&antallMeldinger=1"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBodyFile("tpssynt/tpsSynt_NotNullFields_Response.json")));
        
        List<RsMeldingstype> skdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("aa", 1);
        
        List<String> ignoredFields = Arrays.asList("getSaksid", "getEmbete", "getSakstype",
                "getVedtaksdato", "getInternVergeid", "getVergeFnrDnr", "getVergetype",
                "getMandattype", "getMandatTekst", "getReserverFramtidigBruk");
        AssertionUtils.assertAllFieldsNotNull(((RsMeldingstype1Felter) skdmeldinger.get(0)), ignoredFields);
    }
}