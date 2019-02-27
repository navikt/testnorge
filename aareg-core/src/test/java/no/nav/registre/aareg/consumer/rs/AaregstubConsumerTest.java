package no.nav.registre.aareg.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class AaregstubConsumerTest {

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Test
    public void shouldSendSyntetiskeMeldinger() {
        Map<String, List<Map<String, String>>> syntetiserteMeldinger = new HashMap<>();

        stubAaregstubConsumer();

        ResponseEntity response = aaregstubConsumer.sendTilAaregstub(syntetiserteMeldinger);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubAaregstubConsumer() {
        stubFor(post(urlPathEqualTo("/aaregstub/api/v1/lagreArbeidsforhold"))
                .withRequestBody(equalToJson("{}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
