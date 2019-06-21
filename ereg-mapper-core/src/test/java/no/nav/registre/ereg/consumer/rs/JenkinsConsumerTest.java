package no.nav.registre.ereg.consumer.rs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.permanentRedirect;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {JenkinsConsumer.class})
public class JenkinsConsumerTest {

    @Autowired
    private JenkinsConsumer jenkinsConsumer;

    @Test
    public void send_ok() {
        stubForJenkinsJob_OK();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        boolean isSendt = jenkinsConsumer.send(FLAT_FILE);
        assertTrue(isSendt);
    }

    @Test(expected = HttpServerErrorException.InternalServerError.class)
    public void send_InternalError() {
        stubForJenkinsJob_internalServerError();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        jenkinsConsumer.send(FLAT_FILE);
    }

    @Test
    public void send_300Code() {
        stubForJenkinsJob_300_code();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        boolean isSendt = jenkinsConsumer.send(FLAT_FILE);
        assertFalse(isSendt);
    }

    private void stubForJenkinsJob_OK() {
        stubFor(post(urlEqualTo("/job/Start_BEREG007/")).willReturn(ok()
        ));
    }

    private void stubForJenkinsJob_internalServerError() {
        stubFor(post(urlEqualTo("/job/Start_BEREG007/")).willReturn(serverError()
        ));
    }

    private void stubForJenkinsJob_300_code() {
        stubFor(post(urlEqualTo("/job/Start_BEREG007/")).willReturn(permanentRedirect("")
        ));
    }
}