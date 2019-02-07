package no.nav.registre.sigrun.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

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

    @Test
    public void shouldGetLevendeIdenter() {
        stubHodejegerenConsumer();
        List<String> result = hodejegerenConsumer.finnLevendeIdenter(syntetiserPoppRequest);
        assertThat(result.toString(), containsString(fnr1));
        assertThat(result.toString(), containsString(fnr2));
    }

    public void stubHodejegerenConsumer() {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/levende-identer"
                + "?avspillergruppeId=" + avspillergruppeId
                + "&miljoe=" + miljoe
                + "&antallPersoner=" + fnrs.size()))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnrs.get(0) + "\", \"" + fnrs.get(1) + "\"]")));
    }
}
