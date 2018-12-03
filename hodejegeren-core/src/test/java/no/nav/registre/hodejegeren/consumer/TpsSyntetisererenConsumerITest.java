package no.nav.registre.hodejegeren.consumer;

import static no.nav.registre.hodejegeren.testutils.ResourceUtils.getResourceFileContent;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import no.nav.registre.hodejegeren.testutils.AssertionUtils;

@RunWith(SpringRunner.class)
@RestClientTest(TpsSyntetisererenConsumer.class)
@ActiveProfiles("itest")
public class TpsSyntetisererenConsumerITest {
    
    @Autowired
    private TpsSyntetisererenConsumer consumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${tps-syntetisereren.rest-api.url}")
    private String serverUrl;
    
    /**
     * Tester om metoden bygger korrekt URI og queryParam når den konsumerer Tps Synt.
     */
    @Test
    public void testRequestKonsumeringAvTpsSynt() {
        String endringskode = "0211";
        int antallMeldinger = 1;
        this.server.expect(requestToUriTemplate(serverUrl +
                "/generate?endringskode={endringskode}&antallMeldinger={antall}&service=hodejegeren", endringskode, antallMeldinger))
                .andRespond(withSuccess("[null]", MediaType.APPLICATION_JSON));
        
        consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);
        
        this.server.verify();
    }
    
    /**
     * Tester at alle navnene i forventet responsmelding fra TPS Syntetisereren
     * blir med i java-objektet RsMeldingstype1Felter.
     */
    @Test
    public void shouldDeserialiseAllFieldsInTheResponse() throws InvocationTargetException, IllegalAccessException {
        String endringskode = "0211";
        int antallMeldinger = 1;
        this.server.expect(requestToUriTemplate(serverUrl +
                "/generate?endringskode={endringskode}&antallMeldinger={antall}&service=hodejegeren", endringskode, antallMeldinger))
                .andRespond(withSuccess(getResourceFileContent("__files/tpssynt/tpsSynt_NotNullFields_Response.json"), MediaType.APPLICATION_JSON));
        
        List<RsMeldingstype> skdmeldinger = consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);
        
        List<String> ignoredFields = Arrays.asList("getSaksid", "getEmbete", "getSakstype",
                "getVedtaksdato", "getInternVergeid", "getVergeFnrDnr", "getVergetype",
                "getMandattype", "getMandatTekst", "getReserverFramtidigBruk");
        AssertionUtils.assertAllFieldsNotNull(((RsMeldingstype1Felter) skdmeldinger.get(0)), ignoredFields);
    }
}