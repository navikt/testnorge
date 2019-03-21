package no.nav.registre.aareg.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
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

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class AaregstubConsumerTest {

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Test
    public void shouldGetAlleArbeidstakere() {
        stubAaregstubHentArbeidstakereConsumer();

        List<String> response = aaregstubConsumer.hentEksisterendeIdenter();

        assertThat(response.get(0), equalTo(fnr1));
        assertThat(response.get(1), equalTo(fnr2));
    }

    @Test
    public void shouldSendSyntetiskeMeldinger() {
        List<ArbeidsforholdsResponse> syntetiserteMeldinger = new ArrayList<>();

        stubAaregstubLagreConsumer();

        ResponseEntity response = aaregstubConsumer.sendTilAaregstub(syntetiserteMeldinger);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubAaregstubHentArbeidstakereConsumer() {
        stubFor(get(urlPathEqualTo("/aaregstub/api/v1/hentAlleArbeidstakere"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubAaregstubLagreConsumer() {
        stubFor(post(urlPathEqualTo("/aaregstub/api/v1/lagreArbeidsforhold"))
                .withRequestBody(equalToJson("[]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
