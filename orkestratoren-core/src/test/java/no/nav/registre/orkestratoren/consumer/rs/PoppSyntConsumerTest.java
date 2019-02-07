package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class PoppSyntConsumerTest {

    @Autowired
    private PoppSyntConsumer poppSyntConsumer;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> fnrs;
    private SyntetiserPoppRequest syntetiserPoppRequest;

    @Before
    public void setUp() {
        fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, fnrs.size());
    }

    /**
     * Scenario: Tester happypath til {@link PoppSyntConsumer#startSyntetisering} - forventer at metoden returnerer statuskodene
     * gitt av sigrun-skd-stub. Forventer at metoden kaller Testnorge-Sigrun med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        stubPoppConsumer();

        ResponseEntity response = poppSyntConsumer.startSyntetisering(syntetiserPoppRequest, "test");

        assertThat(response.getBody().toString(), containsString(HttpStatus.OK.toString()));
    }

    public void stubPoppConsumer() {
        stubFor(post(urlPathEqualTo("/sigrun/api/v1/syntetisering/generer"))
                .withHeader("testdataEier", equalTo("test"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + avspillergruppeId
                                + ",\"miljoe\":\"" + miljoe + "\""
                                + ",\"antallIdenter\":" + fnrs.size() + "}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + HttpStatus.OK + "\", \"" + HttpStatus.INTERNAL_SERVER_ERROR + "\"]")));
    }

}
