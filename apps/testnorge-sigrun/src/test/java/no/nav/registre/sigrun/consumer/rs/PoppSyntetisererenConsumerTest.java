package no.nav.registre.sigrun.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.registre.sigrun.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
import java.util.ArrayList;
import java.util.Arrays;

import no.nav.registre.sigrun.domain.PoppSyntetisererenResponse;

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
        var fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        var expectedResponse = Arrays.asList(new ObjectMapper().readValue(Resources.getResource("inntektsmeldinger_test.json"), PoppSyntetisererenResponse[].class));

        stubToken();
        stubPoppSyntetisererenConsumer();

        var result = poppSyntetisererenConsumer.hentPoppMeldingerFromSyntRest(fnrs);

        assertThat(result.get(0).getPersonidentifikator(), equalTo(expectedResponse.get(0).getPersonidentifikator()));
        assertThat(result.get(0).getInntektsaar(), equalTo(expectedResponse.get(0).getInntektsaar()));
        assertThat(result.get(0).getTestdataEier(), equalTo(expectedResponse.get(0).getTestdataEier()));
        assertThat(result.get(0).getTjeneste(), equalTo(expectedResponse.get(0).getTjeneste()));
    }

    public void stubPoppSyntetisererenConsumer() {
        stubFor(post(urlPathEqualTo("/synthdata-popp/api/v1/generate/popp"))
                .withRequestBody(equalToJson("[\"" + fnr1 + "\", \"" + fnr2 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("inntektsmeldinger_test.json"))));
    }

    private void stubToken() {
        stubFor(post("/aad/oauth2/v2.0/token").willReturn(okJson(
                "{\"access_token\": \"dummy\"}"
        )));
    }
}
