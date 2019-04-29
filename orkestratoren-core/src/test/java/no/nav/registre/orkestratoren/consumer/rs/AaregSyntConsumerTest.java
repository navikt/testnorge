package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class AaregSyntConsumerTest {

    @Autowired
    private AaregSyntConsumer aaregSyntConsumer;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private List<String> fnrs;
    private SyntetiserAaregRequest syntetiserAaregRequest;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Before
    public void setUp() {
        fnrs = new ArrayList<>();
        fnrs.addAll(Arrays.asList(fnr1, fnr2));
        syntetiserAaregRequest = new SyntetiserAaregRequest(AVSPILLERGRUPPE_ID, MILJOE, fnrs.size());
    }

    @Test
    public void shouldStartSyntetisering() {
        stubAaregSyntConsumer();

        ResponseEntity response = aaregSyntConsumer.startSyntetisering(syntetiserAaregRequest, false);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubAaregSyntConsumer() {
        stubFor(post(urlPathEqualTo("/aareg/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                                + ",\"miljoe\":\"" + MILJOE + "\""
                                + ",\"antallNyeIdenter\":" + fnrs.size() + "}"))
                .willReturn(ok()));
    }
}