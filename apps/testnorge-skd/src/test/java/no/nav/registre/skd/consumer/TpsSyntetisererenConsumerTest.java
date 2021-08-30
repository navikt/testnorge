package no.nav.registre.skd.consumer;

import static no.nav.registre.skd.testutils.ResourceUtils.getResourceFileContent;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import no.nav.registre.skd.testutils.AssertionUtils;

@RunWith(SpringRunner.class)
@RestClientTest(TpsSyntetisererenConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TpsSyntetisererenConsumerTest {

    @Autowired
    private TpsSyntetisererenConsumer consumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${syntrest.rest.api.url}")
    private String serverUrl;

    /**
     * Tester om metoden bygger korrekt URI og queryParam når den konsumerer Tps Synt.
     */
    @Test
    public void testRequestKonsumeringAvTpsSynt() {
        var endringskode = "0211";
        var antallMeldinger = 1;

        this.server.expect(requestToUriTemplate(serverUrl + "/v1/generate/tps/" + endringskode + "?numToGenerate=" + antallMeldinger))
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
        var endringskode = "0211";
        var antallMeldinger = 1;
        this.server.expect(requestToUriTemplate(serverUrl +
                "/v1/generate/tps/" + endringskode + "?numToGenerate=" + antallMeldinger))
                .andRespond(withSuccess(getResourceFileContent("__files/tpssynt/tpsSynt_NotNullFields_Response.json"), MediaType.APPLICATION_JSON));

        var skdmeldinger = consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);

        var ignoredFields = Arrays.asList("getSaksid", "getEmbete", "getSakstype",
                "getVedtaksdato", "getInternVergeid", "getVergeFnrDnr", "getVergetype",
                "getMandattype", "getMandatTekst", "getReserverFramtidigBruk");
        AssertionUtils.assertAllFieldsNotNull(skdmeldinger.get(0), ignoredFields);
    }
}