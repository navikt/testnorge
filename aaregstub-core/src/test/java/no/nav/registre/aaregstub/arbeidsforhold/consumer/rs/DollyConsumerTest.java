package no.nav.registre.aaregstub.arbeidsforhold.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.aaregstub.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.consumer.rs.responses.DollyResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DollyConsumerTest {

    @Autowired
    private DollyConsumer dollyConsumer;

    private ArbeidsforholdsResponse arbeidsforholdsResponse;
    private Map<String, Object> token;

    @Before
    public void setUp() throws IOException {
        token = new ObjectMapper().readValue(getResourceFileContent("token.json"), new TypeReference<Map<String, Object>>() {
        });
        arbeidsforholdsResponse = ArbeidsforholdsResponse.builder().build();
    }

    @Test
    public void shouldGetTokenForInst2() {
        stubTokenProvider();

        Map<String, Object> actualToken = dollyConsumer.hentTokenTilDolly();

        assertThat(actualToken.get("idToken").toString(), containsString(token.get("idToken").toString()));
    }

    @Test
    public void shouldSendArbeidsforholdToAareg() {
        stubDollyConsumer();

        DollyResponse response = dollyConsumer.sendArbeidsforholdTilAareg(token, arbeidsforholdsResponse);

        assertThat(response.getHttpStatus(), is(HttpStatus.CREATED));
    }

    private void stubTokenProvider() {
        stubFor(get(urlPathEqualTo("/token/user"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("username", equalTo("dummy"))
                .withHeader("password", equalTo("dummy"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("token.json"))));
    }

    private void stubDollyConsumer() {
        stubFor(post(urlPathEqualTo("/dolly/api/v1/aareg/arbeidsforhold"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        .withBody("{\"statusPerMiljoe\": {\"q2\": \"OK\", \"q11\": \"OK\"}}")
                        .withHeader("Content-Type", "application/json")));
    }
}