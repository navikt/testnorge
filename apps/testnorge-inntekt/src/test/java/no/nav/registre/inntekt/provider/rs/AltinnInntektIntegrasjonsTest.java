package no.nav.registre.inntekt.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStreamReader;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
class AltinnInntektIntegrasjonsTest {

    @Value("${path.altinninntekt.satisfactory.json}")
    private String satisfactoryJsonPath;

    @Autowired(required = false)
    protected WebApplicationContext context;


    @Autowired
    private MockMvc mockMvc;

    private String satisfactoryJson;

    private String loadResourceAsString(String fileName) throws IOException {
        return FileCopyUtils
                .copyToString(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
    }

    @BeforeEach
    public void setup() {
        try {
            satisfactoryJson = loadResourceAsString(satisfactoryJsonPath);
        } catch (IOException e) {
            System.err.println("Problemer med lasting av testressurser.");
            e.printStackTrace();
        }
    }

    @AfterEach
    public void cleanup() {
        reset();
    }

    @Test
    @Disabled
    void passingCall() throws Exception {
        stubForInntektsmelding();
        stubForAuthorization();
        stubForJournalpost();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/altinnInntekt/enkeltident?includeXml=true")
                        .content(satisfactoryJson)
                        .header("Nav-Call-Id", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fnr\":\"12345678910\",\"dokumenter\":[{\"journalpostId\":\"1\",\"dokumentInfoId\":\"2\"}]}"));
    }

    @Test
    @Disabled
    void passingCallNoXml() throws Exception {
        stubForInntektsmelding();
        stubForAuthorization();
        stubForJournalpost();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/altinnInntekt/enkeltident")
                        .content(satisfactoryJson)
                        .header("Nav-Call-Id", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fnr\":\"12345678910\",\"dokumenter\":[{\"journalpostId\":\"1\",\"dokumentInfoId\":\"2\"}]}"));
    }


    private void stubForInntektsmelding() {
        stubFor(post(urlEqualTo("/api/v2/inntektsmelding/2018/12/11"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<dummyXml><title>My Dummy</title><content>This is a dummy xml object.</content></dummyXml>")));
    }

    private void stubForAuthorization() {
        stubFor(get(urlEqualTo("/token/?grant_type=client_credentials&scope=openid"))
                .withHeader(AUTHORIZATION, containing("Basic "))
                .willReturn(aResponse()
                        .withStatus(200)));
    }

    private void stubForJournalpost() {
        stubFor(post(urlEqualTo("/rest/journalpostapi/v1/journalpost"))
                .withHeader(AUTHORIZATION, containing("Bearer "))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"journalpostId\": \"1\",\"journalstatus\":\"Ikke relevant\",\"melding\":\"Dokumentene er postet!\",\"journalpostferdigstilt\":true,\"dokumenter\":[{\"brevkode\":\"TEST BREVKODE\",\"dokumentInfoId\":\"2\",\"tittel\":\"TEST TITTEL\"}]}")));

    }

}
