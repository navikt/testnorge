package no.nav.registre.inntekt.provider.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.inntekt.ApplicationStarter;
import no.nav.registre.inntekt.config.AppConfig;
import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock
@AutoConfigureMockMvc
public class AltinnInntektIntegrasjonsTest {

    @Value("${path.altinninntekt.satisfactory.json}")
    private String satisfactoryJsonPath;
    @Value("${path.altinninntekt.failing.json}")
    private String failingJsonPath;
    @Value("${path.altininntektconsumer.satisfactory.json}")
    private String satisfactoryAltinnInntektJsonPath;

    @Value("${altinnInntekt.rest.api.url}")
    private String altinnInntektUrl;

    @Autowired(required = false)
    protected WebApplicationContext context;

    @Autowired
    private AltinnInntektController kontroller;

    @Autowired
    private RestTemplate restTemplate;
    /*private MockRestServiceServer mockServer;*/

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String emptyRequestJson;
    private String satisfactoryJson;
    private String satisfactoryAltinnInntektConsumerJson;
    private String failingJson;

    private String loadResourceAsString(String fileName) throws IOException {
        return FileCopyUtils
                .copyToString(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
    }

    @Before
    public void setup() {
        System.out.println(satisfactoryJsonPath);

        try {
            satisfactoryJson = loadResourceAsString(satisfactoryJsonPath);
            failingJson = loadResourceAsString(failingJsonPath);
            satisfactoryAltinnInntektConsumerJson = loadResourceAsString(satisfactoryAltinnInntektJsonPath);
        } catch (IOException e) {
            System.err.println("Problemer med lasting av testressurser.");
            e.printStackTrace();
        }
        /*mockServer = MockRestServiceServer.createServer(restTemplate);*/
    }

    @Test
    @DirtiesContext
    public void passingCall() throws Exception {
        System.out.println(satisfactoryJson);

         stubFor(post(urlEqualTo(altinnInntektUrl + "/v2/inntektsmelding/2018/12/11"))
                 // .withRequestBody(equalToJson(satisfactoryAltinnInntektConsumerJson))
                 .willReturn(aResponse()
                         .withStatus(200)
                         .withHeader("Content-Type", "text/xml")
                         .withBody("<dummyXml><title>My Dummy</title><content>This is a dummy xml object.</content></dummyXml>")));

        AltinnDollyRequest innkommendeBody = objectMapper.readValue(satisfactoryJson, AltinnDollyRequest.class);
        Boolean continueOnError = null;
        Boolean includeXml = null;
        Boolean validate = null;
//
//        ResponseEntity<?> response;
//        response = kontroller.genererMeldingForIdent(innkommendeBody, validate, includeXml, continueOnError);
//
//        AltinnInntektResponse body = (AltinnInntektResponse) response.getBody();

        var tmp = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/altinnInntekt/enkeltident")
                .content(satisfactoryJson)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        /*mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(altinnInntektUrl + "/v2/inntektsmelding/2018/12/11"))
                .andExpect(content().json(satisfactoryAltinnInntektConsumerJson));*/

    }

}
