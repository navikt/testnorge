package no.nav.registre.ereg.consumer.rs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.permanentRedirect;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import no.nav.registre.ereg.config.AppConfig;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {JenkinsConsumer.class, AppConfig.class})
public class JenkinsConsumerTest {

    @Autowired
    private JenkinsConsumer jenkinsConsumer;

    private final String ENV = "t1";

    @Before
    public void setUp() {
        stubForJenkinsCrumb();
    }

    @Test
    public void send_ok() {
        stubForJenkinsJob_OK();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        boolean isSendt = jenkinsConsumer.send(FLAT_FILE, ENV);
        assertTrue(isSendt);
    }

    @Test(expected = HttpServerErrorException.InternalServerError.class)
    public void send_InternalError() {
        stubForJenkinsJob_internalServerError();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        jenkinsConsumer.send(FLAT_FILE, ENV);
    }

    @Test
    public void send_300Code() {
        stubForJenkinsJob_300_code();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        boolean isSendt = jenkinsConsumer.send(FLAT_FILE, ENV);
        assertFalse(isSendt);
    }

    private void stubForJenkinsJob_OK() {
        stubFor(post(urlEqualTo("/view/Registre/job/Start_BEREG007/buildWithParameters")).willReturn(ok()
        ));
    }

    private void stubForJenkinsJob_internalServerError() {
        stubFor(post(urlEqualTo("/view/Registre/job/Start_BEREG007/buildWithParameters")).willReturn(serverError()
        ));
    }

    private void stubForJenkinsJob_300_code() {
        stubFor(post(urlEqualTo("/view/Registre/job/Start_BEREG007/buildWithParameters")).willReturn(permanentRedirect("")
        ));
    }

    private void stubForJenkinsCrumb() {
        stubFor(get(urlEqualTo("/crumbIssuer/api/json")).willReturn(okJson("{\n" +
                "    \"_class\": \"hudson.security.csrf.DefaultCrumbIssuer\",\n" +
                "    \"crumb\": \"aCrumb\",\n" +
                "    \"crumbRequestField\": \"Jenkins-Crumb\"\n" +
                "}")));
    }
}