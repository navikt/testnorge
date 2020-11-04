package no.nav.registre.skd.commands.tpsf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class HentMeldingerCommandTest {

    public static MockWebServer mockBackEnd;
    private static WebClient webClient;
    private static ObjectMapper objectMapper;

    @Before
    public void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        String baseUrl = String.format("https://localhost:%s", mockBackEnd.getPort());
        webClient = WebClient.builder().baseUrl(baseUrl).build();
        objectMapper = new ObjectMapper();
    }

    @After
    public void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void hentMeldingerEnSide() throws JsonProcessingException {
        stubHentTreMeldinger();
        var requestIds = Collections.nCopies(3, "10101010101");
        var response = new HentMeldingerCommand(webClient, requestIds).call();
        assertThat(response.size(), is(3));
    }


    private void stubHentTreMeldinger() throws JsonProcessingException {
        var mockRsMeldingstype = RsMeldingstype1Felter.builder()
                .tkNr("19203")
                .bibehold("aoweifj")
                .build();
        var mockResponse = objectMapper.writeValueAsString(Collections.nCopies(3, mockRsMeldingstype));
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {
                switch (recordedRequest.getPath()) {
                case "/v1/endringsmelding/skd/meldinger":
                    return new MockResponse().setResponseCode(HttpStatus.OK_200)
                            .addHeader("Content-Type", "application/json")
                            .setBody(mockResponse);
                }
                return new MockResponse().setResponseCode(HttpStatus.BAD_REQUEST_400);
            }
        };
        mockBackEnd.setDispatcher(dispatcher);
    }
}
