package no.nav.registre.sigrun.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.sigrun.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
public class PoppSyntetisererenConsumerTest {

    @Autowired
    private PoppSyntetisererenConsumer poppSyntetisererenConsumer;

    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Test
    public void shouldGetMeldinger() throws IOException {
        List<String> fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        URL jsonContent = Resources.getResource("inntektsmeldinger_test.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        List list = objectMapper.treeToValue(jsonNode, List.class);

        stubPoppSyntetisererenConsumer();

        List<Map<String, Object>> result = poppSyntetisererenConsumer.hentPoppMeldingerFromSyntRest(fnrs);

        assertThat(result, equalTo(list));
    }

    public void stubPoppSyntetisererenConsumer() {
        stubFor(post(urlPathEqualTo("/synthdata-popp/api/v1/generate/popp"))
                .withRequestBody(equalToJson("[\"" + fnr1 + "\", \"" + fnr2 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("inntektsmeldinger_test.json"))));
    }
}
