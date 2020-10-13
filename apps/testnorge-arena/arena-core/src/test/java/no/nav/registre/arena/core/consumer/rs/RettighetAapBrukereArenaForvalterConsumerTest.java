package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;


@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class RettighetAapBrukereArenaForvalterConsumerTest {

    private RettighetArenaForvalterConsumer consumer;

    private MockWebServer mockWebServer;

    private List<RettighetRequest> rettigheter;
    private String fnr = "270699494213";


    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.consumer = new RettighetArenaForvalterConsumer(mockWebServer.url("/").toString());

        RettighetAapRequest aapRequest = new RettighetAapRequest();
        aapRequest.setPersonident(fnr);
        RettighetAap115Request aap115Request = new RettighetAap115Request();
        aap115Request.setPersonident(fnr);
        RettighetUngUfoerRequest ungUfoerRequest = new RettighetUngUfoerRequest();
        ungUfoerRequest.setPersonident(fnr);
        RettighetTvungenForvaltningRequest tvungenForvaltningRequest = new RettighetTvungenForvaltningRequest();
        tvungenForvaltningRequest.setPersonident(fnr);
        RettighetFritakMeldekortRequest fritakMeldekortRequest = new RettighetFritakMeldekortRequest();
        fritakMeldekortRequest.setPersonident(fnr);

        rettigheter = new ArrayList<>(Arrays.asList(
                aapRequest,
                aap115Request,
                ungUfoerRequest,
                tvungenForvaltningRequest,
                fritakMeldekortRequest
        ));
    }

    @Test
    public void shouldOppretteRettighetAap() {
        stubArenaForvalterOpprettRettighet();

        var response = consumer.opprettRettighet(rettigheter);

        assertThat(response.get(fnr).get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr).get(1).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr).get(2).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr).get(3).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr).get(4).getFeiledeRettigheter().size(), equalTo(1));
    }

    private void stubArenaForvalterOpprettRettighet() {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/v1/aap":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(getResourceFileContent("files/aap/aap_forvalter_response.json"));
                    case "/v1/aap115":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(getResourceFileContent("files/aap/aap115_forvalter_response.json"));
                    case "/v1/aapungufor":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(getResourceFileContent("files/aap/ung_ufoer_forvalter_response.json"));
                    case "/v1/aaptvungenforvaltning":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(getResourceFileContent("files/aap/tvungen_forvaltning_forvalter_response.json"));
                    case "/v1/aapfritakmeldekort":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .setBody(getResourceFileContent("files/aap/fritak_meldekort_forvalter_response.json"));
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