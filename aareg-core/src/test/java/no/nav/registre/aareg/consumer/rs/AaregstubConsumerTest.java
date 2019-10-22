package no.nav.registre.aareg.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class AaregstubConsumerTest {

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private String fnr3 = "02020202020";

    @Test
    public void shouldGetAlleArbeidstakere() {
        stubAaregstubHentArbeidstakereConsumer();

        List<String> response = aaregstubConsumer.hentEksisterendeIdenter();

        assertThat(response.get(0), equalTo(fnr1));
        assertThat(response.get(1), equalTo(fnr2));
        assertThat(response.get(2), equalTo(fnr3));
    }

    @Test
    public void shouldSendSyntetiskeMeldinger() {
        List<RsAaregOpprettRequest> syntetiserteMeldinger = new ArrayList<>();

        stubAaregstubLagreConsumer();

        List<String> statusFraAaregstubResponse = aaregstubConsumer.sendTilAaregstub(syntetiserteMeldinger);

        assertThat(statusFraAaregstubResponse.size(), equalTo(2));
        assertThat(statusFraAaregstubResponse.get(0), equalTo(fnr1));
        assertThat(statusFraAaregstubResponse.get(1), equalTo(fnr2));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        stubAaregstubWithEmptyBody();

        Logger logger = (Logger) LoggerFactory.getLogger(AaregstubConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        aaregstubConsumer.hentEksisterendeIdenter();

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra Aaregstub: NullPointerException"));
    }

    private void stubAaregstubHentArbeidstakereConsumer() {
        stubFor(get(urlPathEqualTo("/aaregstub/api/v1/hentAlleArbeidstakere"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\", \"" + fnr3 + "\"]")));
    }

    private void stubAaregstubLagreConsumer() {
        stubFor(post(urlPathEqualTo("/aaregstub/api/v1/lagreArbeidsforhold"))
                .withRequestBody(equalToJson("[]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubAaregstubWithEmptyBody() {
        stubFor(get(urlPathEqualTo("/aaregstub/api/v1/hentAlleArbeidstakere"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
