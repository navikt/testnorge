package no.nav.registre.inst.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.consumer.rs.credential.SyntInstGcpProperties;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;

import static no.nav.registre.inst.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class InstSyntetisererenConsumerTest {

    private InstSyntetisererenConsumer consumer;

    private MockWebServer mockWebServer;

    private final int numToGenerate = 2;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getSyntInstDispatcher();
        mockWebServer.setDispatcher(dispatcher);

        var creds = new SyntInstGcpProperties();
        creds.setUrl(mockWebServer.url("/synt-inst").toString());
        creds.setCluster("test");
        creds.setNamespace("test");
        creds.setName("synthdata-inst-gcp-test");

        this.consumer = new InstSyntetisererenConsumer(
                creds,
                new AccessTokenService(null, mockWebServer.url("/token").toString(), null,
                        new AzureClientCredentials("dummy", "dummy")));

    }

    @Test
    public void shouldGetMeldinger() {
        var result = consumer.hentInstMeldingerFromSyntRest(numToGenerate);

        assertThat(result.size(), is(numToGenerate));
        assertThat(result.get(0).getTssEksternId(), equalTo("440"));
        assertThat(result.get(1).getTssEksternId(), equalTo("441"));
    }

    private Dispatcher getSyntInstDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                return switch (request.getPath()) {
                    case "/synt-inst/api/v1/generate/inst/" + numToGenerate -> new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody(getResourceFileContent("institusjonsmelding.json"));
                    case "/token/oauth2/v2.0/token" -> new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json")
                            .setBody("{\"access_token\": \"dummy\"}");
                    default -> new MockResponse().setResponseCode(404);
                };
            }
        };
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}