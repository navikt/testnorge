package no.nav.registre.medl.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.medl.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class MedlSyntConsumerTest {

    @Autowired
    private MedlSyntConsumer medlSyntConsumer;

    private int numToGenerate = 2;

    @Test
    public void shouldGetMeldinger() {
        stubMedlSyntetisererenConsumer();
        var result = medlSyntConsumer.hentMedlemskapsmeldingerFromSyntRest(numToGenerate);

        assertThat(result.size(), is(numToGenerate));
        assertThat(result.get(0).getGrunnlag(), equalTo("FTL_2-5"));
        assertThat(result.get(1).getGrunnlag(), equalTo("FTL_2-5"));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        Logger logger = (Logger) LoggerFactory.getLogger(MedlSyntConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubMedlSyntetisererenConsumerWithEmptyBody();

        medlSyntConsumer.hentMedlemskapsmeldingerFromSyntRest(numToGenerate);

        assertThat(listAppender.list.size(), is(equalTo(1)));
    }

    private void stubMedlSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-medl/api/v1/generate/medl?numToGenerate=" + numToGenerate))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("medlemskapsmelding.json"))));
    }

    private void stubMedlSyntetisererenConsumerWithEmptyBody() {
        stubFor(get(urlEqualTo("/synthdata-medl/api/v1/generate/medl?numToGenerate=" + numToGenerate))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}