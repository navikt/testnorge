package no.nav.registre.sam.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sam.consumer.rs.credential.SyntSamGcpProperties;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;

import static no.nav.registre.sam.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class SamSyntetisererenConsumerTest {

    private SamSyntetisererenConsumer consumer;

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getSyntSamDispatcher();
        mockWebServer.setDispatcher(dispatcher);

        var creds = new SyntSamGcpProperties();
        creds.setUrl(mockWebServer.url("/syntsam").toString());
        creds.setCluster("test");
        creds.setNamespace("test");
        creds.setName("synthdata-sam-gcp-test");

        this.consumer = new SamSyntetisererenConsumer(
                creds,
                new AccessTokenService(null, mockWebServer.url("/token").toString(), null,
                        new AzureClientCredentials("dummy", "dummy")));

    }

    @Test
    public void shouldGetSyntetiserteMeldinger() {
        var response = consumer.hentSammeldingerFromSyntRest(2);

        assertThat(response.get(0).getDatoEndret(), equalTo("10.02.2010"));
        assertThat(response.get(0).getKSamHendelseT(), equalTo("VEDTAKNAV"));
        assertThat(response.get(1).getDatoEndret(), equalTo("29.07.2009"));
        assertThat(response.get(1).getKSamHendelseT(), equalTo("VEDTAKNAV"));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(SamSyntetisererenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        consumer.hentSammeldingerFromSyntRest(1);

        assertThat(listAppender.list.size(), is(CoreMatchers.equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra synthdata-sam: NullPointerException"));
    }

    private Dispatcher getSyntSamDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/syntsam//api/v1/generate_sam/2":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody(getResourceFileContent("samordningsmelding.json"));
                    case "/syntsam//api/v1/generate_sam/1":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json");
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