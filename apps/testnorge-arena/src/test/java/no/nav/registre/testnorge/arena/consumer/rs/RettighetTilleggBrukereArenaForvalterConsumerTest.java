package no.nav.registre.testnorge.arena.consumer.rs;

import static no.nav.registre.testnorge.arena.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class RettighetTilleggBrukereArenaForvalterConsumerTest {

    private RettighetArenaForvalterConsumer consumer;

    private MockWebServer mockWebServer;

    private List<RettighetRequest> rettigheter;
    private String fnr = "270699494213";

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.consumer = new RettighetArenaForvalterConsumer(mockWebServer.url("/").toString());

        RettighetTilleggRequest tilleggRequest = new RettighetTilleggRequest();
        tilleggRequest.setPersonident(fnr);
        rettigheter = new ArrayList<>(Collections.singletonList(
                tilleggRequest
        ));
    }

    @Test
    public void shouldOppretteRettighetTillegg() {
        stubArenaForvalterOpprettRettighetTilleggLaeremidler();

        var response = consumer.opprettRettighet(rettigheter);

        assertThat(response.get(fnr).get(0).getNyeRettigheterTillegg().size(), equalTo(1));
    }

    private void stubArenaForvalterOpprettRettighetTilleggLaeremidler() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(getResourceFileContent("files/tillegg/tillegg_forvalter_response.json"))
                .setResponseCode(200);

        mockWebServer.enqueue(mockResponse);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}