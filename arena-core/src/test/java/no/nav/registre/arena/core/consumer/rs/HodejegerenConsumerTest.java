package no.nav.registre.arena.core.consumer.rs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private int MINIMUM_ALDER = 16;
    private long gruppeId = 10L;
    private String fnr1 = "10101010101", fnr2 = "20202020202";

    @Test
    public void shouldFindLevendeIdenterOverAlder() {
        stubHodejegerenConsumer();

        List<String> levendeIdenterOverAlder = hodejegerenConsumer.finnLevendeIdenterOverAlder(gruppeId);

        assertThat(levendeIdenterOverAlder, hasItem(fnr1));
        assertThat(levendeIdenterOverAlder, hasItem(fnr2));
    }



    private void stubHodejegerenConsumer() {
        stubFor(get(urlPathEqualTo(
                "/hodejegeren/api/v1/levende-identer-over-alder/" +
                        gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubHodejegerenConsumerEmptyResponse() {
        stubFor(get(urlPathEqualTo(
                "/hodejegeren/api/v1/levende-identer-over-alder/" +
                        gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubHodejegerenConsumerInvalidResponse() {
        stubFor(get(urlPathEqualTo(
                "/hodejegeren/api/v1/levende-identer-over-alder/" +
                        gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "text/html")
                        .withBody("Can you handle unexpected response types?")));
    }

}