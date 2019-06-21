package no.nav.registre.ereg.consumer.rs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {JenkinsConsumer.class})
public class JenkinsConsumerTest {

    @Autowired
    private JenkinsConsumer jenkinsConsumer;

    @Test
    public void send() {
        stubForJenkinsJob_OK();

        String FLAT_FILE = "HEADER 2019062000000AA A\n" +
                "ENH 123      BEDNNY   2019062020190620J           \n" +
                "TRAIER 0000000100000003\n";
        jenkinsConsumer.send(FLAT_FILE);
    }

    private void stubForJenkinsJob_OK() {
        stubFor(post(urlEqualTo("/job/Start_BEREG007/")).willReturn(ok()
        ));
    }
}