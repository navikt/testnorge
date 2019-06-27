package no.nav.registre.arena.core.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.arena.core.config.AppConfig;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {HodejegerenConsumer.class, AppConfig.class})
@EnableAutoConfiguration
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private int MINIMUM_ALDER = 16;
    private long gruppeId = 10L;
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";

    @Test
    public void shouldFindLevendeIdenterOverAlder() {
        stubHodejegerenConsumer();

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(gruppeId);

        assertThat(levendeIdenter, hasItem(fnr1));
        assertThat(levendeIdenter, hasItem(fnr2));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(HodejegerenConsumer.class);
        logger.addAppender(listAppender);

        stubHodejegerenConsumerWithEmptyBody();

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(gruppeId);

        assertThat(listAppender.list.size(), is(Matchers.equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra Hodejegeren: NullPointerException"));
    }



    private void stubHodejegerenConsumer() {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/levende-identer-over-alder/" + gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubHodejegerenConsumerWithEmptyBody() {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/levende-identer-over-alder/" + gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
