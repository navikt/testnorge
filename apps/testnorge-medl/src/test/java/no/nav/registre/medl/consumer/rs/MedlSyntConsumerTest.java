package no.nav.registre.medl.consumer.rs;

import static no.nav.registre.medl.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.medl.consumer.rs.credential.SyntMedlGcpProperties;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class MedlSyntConsumerTest {

    private MedlSyntConsumer consumer;

    private MockWebServer mockWebServer;

    private int numToGenerate = 2;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getSyntMedlDispatcher();
        mockWebServer.setDispatcher(dispatcher);

        var creds = new SyntMedlGcpProperties();
        creds.setUrl(mockWebServer.url("/synt-medl").toString());
        creds.setCluster("test");
        creds.setNamespace("test");
        creds.setName("synthdata-tps-medl-test");

        this.consumer = new MedlSyntConsumer(
                creds,
                new AccessTokenService(null, mockWebServer.url("/token").toString(), null,
                        new AzureClientCredentials("dummy", "dummy")));

    }

    @Test
    public void shouldGetMeldinger() {
        var result = consumer.hentMedlemskapsmeldingerFromSyntRest(numToGenerate);

        assertThat(result.size(), is(numToGenerate));
        assertThat(result.get(0).getGrunnlag(), equalTo("FTL_2-5"));
        assertThat(result.get(1).getGrunnlag(), equalTo("FTL_2-5"));
    }

    private Dispatcher getSyntMedlDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/synt-medl/api/v1/generate/medl/2":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody(getResourceFileContent("medlemskapsmelding.json"));
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