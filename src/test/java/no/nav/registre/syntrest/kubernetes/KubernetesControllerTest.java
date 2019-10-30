package no.nav.registre.syntrest.kubernetes;

import no.nav.registre.syntrest.config.AppConfig;
import no.nav.registre.syntrest.config.KubernetesConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@ContextConfiguration(classes = {
        KubernetesController.class, KubernetesConfig.class, AppConfig.class})
@EnableAutoConfiguration
public class KubernetesControllerTest {

    @Autowired
    private KubernetesController kubernetesController;

    // Sjekk return hvis det tar for lang tid å deploye
    @Test
    public void deploymentTime() {

    }

    @Test
    public void isAliveTest() {
        stubFor(get(urlEqualTo("/MYAPP/internal/isAlive"))
                .willReturn(ok("1")));
        assertThat(kubernetesController.isAlive("MYAPP"), is(true));
    }

    @Test
    public void isNotAliveTest() {
        stubFor(get(urlEqualTo("/MYAPP/internal/isAlive"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("404 Not Found")));
        assertThat(kubernetesController.isAlive("MYAPP"), is(false));
    }

}
