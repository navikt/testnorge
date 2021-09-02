package no.nav.registre.skd.consumer;

import static no.nav.registre.skd.testutils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import no.nav.registre.skd.consumer.credential.SyntTpsGcpProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import no.nav.registre.skd.testutils.AssertionUtils;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class SyntTpsConsumerTest {

    private SyntTpsConsumer consumer;

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getSyntTpsDispatcher();
        mockWebServer.setDispatcher(dispatcher);

        this.consumer = new SyntTpsConsumer(
                new SyntTpsGcpProperties(),
                new AccessTokenService(null, mockWebServer.url("/token").toString(), null,
                        new AzureClientCredentials("dummy", "dummy")),
                mockWebServer.url("/api").toString());

    }

    /**
     * Tester om metoden bygger korrekt URI og queryParam når den konsumerer Tps Synt.
     */
    @Test
    public void testRequestKonsumeringAvTpsSynt() {
        var endringskode = "0211";
        var antallMeldinger = 1;

        var response = consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);

        assertThat(response).hasSize(1);
        assertThat(response.get(0)).isNull();
    }

    /**
     * Tester at alle navnene i forventet responsmelding fra TPS Syntetisereren
     * blir med i java-objektet RsMeldingstype1Felter.
     */
    @Test
    public void shouldDeserialiseAllFieldsInTheResponse() throws InvocationTargetException, IllegalAccessException {
        var endringskode = "0110";
        var antallMeldinger = 1;

        var skdmeldinger = consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);

        var ignoredFields = Arrays.asList("getSaksid", "getEmbete", "getSakstype",
                "getVedtaksdato", "getInternVergeid", "getVergeFnrDnr", "getVergetype",
                "getMandattype", "getMandatTekst", "getReserverFramtidigBruk");
        AssertionUtils.assertAllFieldsNotNull(skdmeldinger.get(0), ignoredFields);
    }

    private Dispatcher getSyntTpsDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                case "/api/v1/generate/tps/0211?numToGenerate=1":
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody("[null]");
                case "/api/v1/generate/tps/0110?numToGenerate=1":
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody(getResourceFileContent("__files/tpssynt/tpsSynt_NotNullFields_Response.json"));
                case "/token/oauth2/v2.0/token":
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody("{\"access_token\": \"dummy\"}");
                }
                return new MockResponse().setResponseCode(404);
            }
        };
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}