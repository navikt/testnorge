package no.nav.registre.aareg.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class AaregSyntetisererenConsumerTest {

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    private String fnr1 = "12345678910";

    @Test
    public void shouldGetSyntetiserteMeldinger() throws IOException {
        List<String> fnrs = new ArrayList<>(Arrays.asList(fnr1));

        URL jsonContent = Resources.getResource("arbeidsforholdsmelding.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        Map<String, List<Map<String, String>>> expected = (Map<String, List<Map<String, String>>>) objectMapper.treeToValue(jsonNode, Map.class);

        stubAaregSyntetisererenConsumer();

        Map<String, List<Map<String, String>>> result = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        // Sjekker innholdet i hele datastrukturen:
        for (Map.Entry<String, List<Map<String, String>>> entry : expected.entrySet()) {
            assertThat(result.keySet().toString(), containsString(entry.getKey()));
            for (int i = 0; i < entry.getValue().size(); i++) {
                for (Map.Entry<String, String> element : entry.getValue().get(i).entrySet()) {
                    assertThat(result.get(entry.getKey()).get(i).keySet().toString(), containsString(element.getKey()));
                    String expectedElementValue = String.valueOf(element.getValue());
                    if (expectedElementValue.equals("null")) {
                        expectedElementValue = null;
                    }
                    assertThat(result.get(entry.getKey()).get(i).get(element.getKey()), equalTo(expectedElementValue));
                }
            }
        }
    }

    public void stubAaregSyntetisererenConsumer() {
        stubFor(post(urlPathEqualTo("/synthdata-aareg/api/v1/generate/aareg"))
                .withRequestBody(equalToJson("[\"" + fnr1 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arbeidsforholdsmelding.json"))));
    }
}
