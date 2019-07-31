package no.nav.registre.bisys.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.bisys.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.bisys.ApplicationStarter;
import no.nav.registre.bisys.LocalApplicationStarter;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ApplicationStarter.class)
@ComponentScan(
    excludeFilters = {@Filter(type = ASSIGNABLE_TYPE, value = LocalApplicationStarter.class)})
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class BisysSyntetisererenConsumerTest {

  @Autowired private BisysSyntetisererenConsumer bisysSyntetisererenConsumer;

  private int antallMeldinger = 2;

  @Test
  public void shouldGetSyntetiserteMeldinger() {
    stubBisysSyntetisererenConsumer();

    List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger =
        bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(antallMeldinger);

    assertThat(syntetiserteBidragsmeldinger.get(0).getBarnetsFnr(), equalTo("01010101010"));
    assertThat(syntetiserteBidragsmeldinger.get(0).getBidragsmottaker(), equalTo("02020202020"));
    assertThat(syntetiserteBidragsmeldinger.get(0).getBidragspliktig(), equalTo("03030303030"));
    assertThat(syntetiserteBidragsmeldinger.get(1).getBarnetsFnr(), equalTo("04040404040"));
    assertThat(syntetiserteBidragsmeldinger.get(1).getBidragsmottaker(), equalTo("05050505050"));
    assertThat(syntetiserteBidragsmeldinger.get(1).getBidragspliktig(), equalTo("06060606060"));
  }

  @Test
  public void shouldLogOnEmptyResponse() {
    Logger logger = (Logger) LoggerFactory.getLogger(BisysSyntetisererenConsumer.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    stubBisysSyntetisererenConsumerWithEmptyBody();

    bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(antallMeldinger);

    assertThat(listAppender.list.size(), is(equalTo(1)));
    assertThat(
        listAppender.list.get(0).toString(),
        containsString("Kunne ikke hente response body fra synthdata-bisys: NullPointerException"));
  }

  private void stubBisysSyntetisererenConsumer() {
    stubFor(
        get(urlEqualTo("/synthdata-bisys/api/v1/generate/bisys?numToGenerate=" + antallMeldinger))
            .willReturn(
                ok().withHeader("Content-Type", "application/json")
                    .withBody(getResourceFileContent("bidragsmelding.json"))));
  }

  private void stubBisysSyntetisererenConsumerWithEmptyBody() {
    stubFor(
        get(urlEqualTo("/synthdata-bisys/api/v1/generate/bisys?numToGenerate=" + antallMeldinger))
            .willReturn(ok().withHeader("Content-Type", "application/json")));
  }
}
