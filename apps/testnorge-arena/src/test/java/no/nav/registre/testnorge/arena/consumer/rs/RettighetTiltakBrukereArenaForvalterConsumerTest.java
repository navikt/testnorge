package no.nav.registre.testnorge.arena.consumer.rs;

import static no.nav.registre.testnorge.arena.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltakspengerRequest;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class RettighetTiltakBrukereArenaForvalterConsumerTest {

    private RettighetArenaForvalterConsumer consumer;

    private MockWebServer mockWebServer;

    private List<RettighetRequest> rettigheter;
    private String fnr1 = "270699494213";

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.consumer = new RettighetArenaForvalterConsumer(mockWebServer.url("/").toString());

        RettighetTiltaksdeltakelseRequest tiltaksdeltakelseRequest = new RettighetTiltaksdeltakelseRequest();
        tiltaksdeltakelseRequest.setPersonident(fnr1);
        RettighetTiltakspengerRequest tiltakspengerRequest = new RettighetTiltakspengerRequest();
        tiltakspengerRequest.setPersonident(fnr1);

        rettigheter = new ArrayList<>(Arrays.asList(
                tiltaksdeltakelseRequest,
                tiltakspengerRequest
        ));
    }

    @Test
    public void shouldOppretteRettighetTiltak() {
        stubArenaForvalterOpprettRettighet();

        var response = consumer.opprettRettighet(rettigheter);

        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter().size(), equalTo(0));
        assertThat(response.get(fnr1).get(1).getNyeRettigheterTiltak().size(), equalTo(1));
    }

    private void stubArenaForvalterOpprettRettighet() {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                case "/v1/tiltaksdeltakelse":
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json; charset=utf-8")
                            .setBody(getResourceFileContent("files/tiltak/tiltaksdeltakelse_forvalter_response.json"));
                case "/v1/tiltakspenger":
                    return new MockResponse().setResponseCode(200)
                            .addHeader("Content-Type", "application/json; charset=utf-8")
                            .setBody(getResourceFileContent("files/tiltak/tiltakspenger_forvalter_response.json"));
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}