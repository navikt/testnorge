package no.nav.registre.bisys.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.bisys.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
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
import no.nav.registre.bisys.consumer.rs.responses.relasjon.RelasjonsResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ApplicationStarter.class)
@ComponentScan(
    excludeFilters = {@Filter(type = ASSIGNABLE_TYPE, value = LocalApplicationStarter.class)})
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

  @Autowired private HodejegerenConsumer hodejegerenConsumer;

  private long gruppeId = 10L;
  private String fnr1 = "01010101010";
  private String fnr2 = "02020202020";
  private String fnrBarn = "11021950000";
  private String miljoe = "t1";

  @Test
  public void shouldFindLevendeIdenter() {
    stubHodejegerenConsumerFinnFoedteIdenter();

    List<String> levendeIdenter = hodejegerenConsumer.finnFoedteIdenter(gruppeId);

    assertThat(levendeIdenter, hasItem(fnr1));
    assertThat(levendeIdenter, hasItem(fnr2));
  }

  @Test
  public void shouldLogOnEmptyResponseFindLevendeIdenter() {
    Logger logger = (Logger) LoggerFactory.getLogger(HodejegerenConsumer.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    stubHodejegerenConsumerWithEmptyBody("/hodejegeren/api/v1/foedte-identer/" + gruppeId);

    hodejegerenConsumer.finnFoedteIdenter(gruppeId);

    assertThat(listAppender.list.size(), is(equalTo(1)));
    assertThat(
        listAppender.list.get(0).toString(),
        containsString("Kunne ikke hente response body fra Hodejegeren: NullPointerException"));
  }

  @Test
  public void shouldHenteRelasjonerTilIdent() {
    String fnrFar = "16120680637";
    String fnrMor = "11020681073";

    stubHodejegerenConsumerHentRelasjoner(fnrBarn, miljoe);

    RelasjonsResponse response = hodejegerenConsumer.hentRelasjonerTilIdent(fnrBarn, miljoe);

    assertThat(response.getFnr(), equalTo(fnrBarn));
    assertThat(response.getRelasjoner().get(0).getFnrRelasjon(), equalTo(fnrFar));
    assertThat(response.getRelasjoner().get(1).getFnrRelasjon(), equalTo(fnrMor));
  }

  @Test
  public void shouldLogOnEmptyResponseHenteRelasjonerTilIdent() {
    Logger logger = (Logger) LoggerFactory.getLogger(HodejegerenConsumer.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    stubHodejegerenConsumerWithEmptyBody(
        "/hodejegeren/api/v1/relasjoner-til-ident?ident=" + fnrBarn + "&miljoe=" + miljoe);

    hodejegerenConsumer.hentRelasjonerTilIdent(fnrBarn, miljoe);

    assertThat(listAppender.list.size(), is(equalTo(1)));
    assertThat(
        listAppender.list.get(0).toString(),
        containsString("Kunne ikke hente response body fra Hodejegeren: NullPointerException"));
  }

  private void stubHodejegerenConsumerFinnFoedteIdenter() {
    stubFor(
        get(urlPathEqualTo("/hodejegeren/api/v1/foedte-identer/" + gruppeId))
            .willReturn(
                ok().withHeader("Content-Type", "application/json")
                    .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
  }

  private void stubHodejegerenConsumerWithEmptyBody(String url) {
    stubFor(get(urlEqualTo(url)).willReturn(ok().withHeader("Content-Type", "application/json")));
  }

  private void stubHodejegerenConsumerHentRelasjoner(String ident, String miljoe) {
    stubFor(
        get(urlEqualTo(
                "/hodejegeren/api/v1/relasjoner-til-ident?ident=" + ident + "&miljoe=" + miljoe))
            .willReturn(
                ok().withHeader("Content-Type", "application/json")
                    .withBody(getResourceFileContent("relasjon.json"))));
  }
}
