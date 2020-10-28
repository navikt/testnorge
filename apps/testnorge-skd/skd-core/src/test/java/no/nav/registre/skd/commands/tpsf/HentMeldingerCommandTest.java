package no.nav.registre.skd.commands.tpsf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
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
    public void hentMeldingerEnSide() {
        var mockRsMeldingstype = RsMeldingstype1Felter.builder()
                .tkNr("19203")
                .bibehold("aoweifj")
                .build();
        var mockResponse = Collections.nCopies(70, mockRsMeldingstype);
        var requestIds = Collections.nCopies(70, "10101010101");
        try {
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(mockResponse))
                    .addHeader("Content-Type", "application/json"));
        } catch (JsonProcessingException e) {
            fail("Object Mapper kunne ikke mappe RsMeldingstype1Felter-liste");
        }

        var response = new HentMeldingerCommand(webClient, requestIds).call();
        assertThat(response.size(), is(70));
    }

}
