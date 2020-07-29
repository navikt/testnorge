package no.nav.registre.sam.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.sam.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class SamSyntetisererenConsumerTest {

    @Autowired
    private SamSyntetisererenConsumer samSyntetisererenConsumer;

    private int antallMeldinger = 2;

    @Test
    public void shouldGetSyntetiserteMeldinger() {
        stubSamSyntetisererenConsumer();

        var response = samSyntetisererenConsumer.hentSammeldingerFromSyntRest(antallMeldinger);

        assertThat(response.get(0).getDatoEndret(), equalTo("10.02.2010"));
        assertThat(response.get(0).getKSamHendelseT(), equalTo("VEDTAKNAV"));
        assertThat(response.get(1).getDatoEndret(), equalTo("29.07.2009"));
        assertThat(response.get(1).getKSamHendelseT(), equalTo("VEDTAKNAV"));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(SamSyntetisererenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubSamSyntetisererenConsumerWithEmptyBody();

        samSyntetisererenConsumer.hentSammeldingerFromSyntRest(antallMeldinger);

        assertThat(listAppender.list.size(), is(CoreMatchers.equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra synthdata-sam: NullPointerException"));
    }

    private void stubSamSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-sam/api/v1/generate/sam?numToGenerate=" + antallMeldinger))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("samordningsmelding.json"))));
    }

    private void stubSamSyntetisererenConsumerWithEmptyBody() {
        stubFor(get(urlEqualTo("/synthdata-sam/api/v1/generate/sam?numToGenerate=" + antallMeldinger))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}