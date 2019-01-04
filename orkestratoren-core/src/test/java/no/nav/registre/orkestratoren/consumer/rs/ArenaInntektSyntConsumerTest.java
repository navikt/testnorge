package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(SpringRunner.class)
@RestClientTest(ArenaInntektSyntConsumer.class)
@TestPropertySource(locations = "classpath:unittest/provider/internalController.properties")
public class ArenaInntektSyntConsumerTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ArenaInntektSyntConsumer arenaInntektSyntConsumer;

    private List<String> inntektsmldMottakere;
    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @Before
    public void setUp() {
        inntektsmldMottakere = new ArrayList<>();
        inntektsmldMottakere.add("Something");
        inntektsmldMottakere.add("Something else");

        logger = (Logger) LoggerFactory.getLogger(ArenaInntektSyntConsumer.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    public void asyncBestillEnInntektsmeldingPerFnrIInntektstubShouldLogOnSuccess() throws InterruptedException {
        this.server.expect(requestTo("https://dummyUrl.inntekt.synt/api/v1/generate")).andRespond(withSuccess());

        arenaInntektSyntConsumer.asyncBestillEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);

        Thread.sleep(2000); // gi async-metoden tid til å bli ferdig

        this.server.verify();

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("synth-arena-inntekt har fullført bestillingen som ble sendt"));
        assertThat(listAppender.list.get(0).toString(), containsString("Antall inntektsmeldinger opprettet i inntekts-stub: " + inntektsmldMottakere.size()));
    }

    @Test
    public void asyncBestillEnInntektsmeldingPerFnrIInntektstubShouldLogOnError() throws InterruptedException {
        this.server.expect(requestTo("https://dummyUrl.inntekt.synt/api/v1/generate")).andRespond(withServerError());

        arenaInntektSyntConsumer.asyncBestillEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);

        Thread.sleep(2000); // gi async-metoden tid til å bli ferdig

        this.server.verify();

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("synth-arena-inntekt returnerte feilmeldingen"));
        assertThat(listAppender.list.get(0).toString(), containsString("Bestillingen ble sendt"));
    }
}