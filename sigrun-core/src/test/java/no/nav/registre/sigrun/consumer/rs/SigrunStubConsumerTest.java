package no.nav.registre.sigrun.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.sigrun.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class SigrunStubConsumerTest {

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private List meldinger;
    private JsonNode jsonNode;
    private HttpStatus statusCodeOk = HttpStatus.OK;
    private HttpStatus statusCodeInternalServerError = HttpStatus.INTERNAL_SERVER_ERROR;


    @Before
    public void setUp() throws IOException {
        URL jsonContent = Resources.getResource("inntektsmeldinger_test.json");
        ObjectMapper objectMapper = new ObjectMapper();
        jsonNode = objectMapper.readTree(jsonContent);
        meldinger = objectMapper.treeToValue(jsonNode, List.class);
    }

    @Test
    public void shouldSendDataToSigrunStub() {
        stubSigrunStubConsumer();

        ResponseEntity result = sigrunStubConsumer.sendDataToSigrunstub(meldinger);

        assertThat(result.getStatusCode(), equalTo(HttpStatus.OK));
        assertNotNull(result.getBody());
        assertThat(result.getBody().toString(), containsString(statusCodeOk.toString()));
        assertThat(result.getBody().toString(), containsString(statusCodeInternalServerError.toString()));
    }

    public void stubSigrunStubConsumer() {
        stubFor(post(urlPathEqualTo("/sigrunstub/testdata/opprettBolk"))
                .withRequestBody(equalToJson(getResourceFileContent("inntektsmeldinger_test.json")))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(("[" + statusCodeOk + ", " + statusCodeInternalServerError + "]"))));
    }
}
