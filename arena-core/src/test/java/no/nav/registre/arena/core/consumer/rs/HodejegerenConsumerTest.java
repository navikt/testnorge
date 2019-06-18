package no.nav.registre.arena.core.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.CoreMatchers.equalTo;

import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private long avspillergruppeId =  10L;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private static final int MINIMUM_ALDER = 16;

    @Test
    public void shouldFindLevendeIdenterOverAlder() {

        stubHodejegerenConsumer();

        List<String> levendeIdenterOverAlder = hodejegerenConsumer.finnLevendeIdenterOverAlder(avspillergruppeId);

        assertThat(levendeIdenterOverAlder, hasItem(fnr1));
        assertThat(levendeIdenterOverAlder, hasItem(fnr2));
    }

    @Test
    public void souldLogOnEmptyResponse() {
        Logger logger = (Logger) LoggerFactory.getLogger(HodejegerenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubHodejegerenConsumerWithEmptyResponse();

        hodejegerenConsumer.finnLevendeIdenterOverAlder(avspillergruppeId);

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra Hodejegeren: NullPointerException"));
    }


    private void stubHodejegerenConsumer() {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/levende-identer-over-alder/" + avspillergruppeId +
                                    "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubHodejegerenConsumerWithEmptyResponse() {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/levende-identer-over-alder/" + avspillergruppeId +
                "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok().withHeader("Content-Type", "application/json")));
    }
}
