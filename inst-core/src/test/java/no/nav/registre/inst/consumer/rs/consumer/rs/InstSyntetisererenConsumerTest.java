package no.nav.registre.inst.consumer.rs.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
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
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.inst.consumer.rs.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class InstSyntetisererenConsumerTest {

    @Autowired
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    private int numToGenerate = 2;

    @Test
    public void shouldGetMeldinger() {
        stubInstSyntetisererenConsumer();
        List<Institusjonsforholdsmelding> result = instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(numToGenerate);

        assertThat(result.size(), is(numToGenerate));
        assertThat(result.get(0).getTssEksternId(), equalTo("440"));
        assertThat(result.get(1).getTssEksternId(), equalTo("441"));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        Logger logger = (Logger) LoggerFactory.getLogger(InstSyntetisererenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubInstSyntetisererenConsumerWithEmptyBody();

        instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(numToGenerate);

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra synthdata-inst: NullPointerException"));
    }

    private void stubInstSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-inst/api/v1/generate/inst?numToGenerate=" + numToGenerate))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmelding.json"))));
    }

    private void stubInstSyntetisererenConsumerWithEmptyBody() {
        stubFor(get(urlEqualTo("/synthdata-inst/api/v1/generate/inst?numToGenerate=" + numToGenerate))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}