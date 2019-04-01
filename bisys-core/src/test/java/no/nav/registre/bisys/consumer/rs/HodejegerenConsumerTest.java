package no.nav.registre.bisys.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private long gruppeId = 10L;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Test
    public void shouldFindLevendeIdenter() {
        stubHodejegerenConsumer();

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenter(gruppeId);

        assertThat(levendeIdenter, hasItem(fnr1));
        assertThat(levendeIdenter, hasItem(fnr2));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        Logger logger = (Logger) LoggerFactory.getLogger(HodejegerenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubHodejegerenConsumerWithEmptyBody();

        hodejegerenConsumer.finnLevendeIdenter(gruppeId);

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra Hodejegeren: NullPointerException"));
    }

    private void stubHodejegerenConsumer() {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/foedte-identer/" + gruppeId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubHodejegerenConsumerWithEmptyBody() {
        stubFor(get(urlPathEqualTo("/hodejegeren/api/v1/foedte-identer/" + gruppeId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
