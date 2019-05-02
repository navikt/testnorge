package no.nav.registre.skd.consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import no.nav.registre.skd.consumer.HodejegerenConsumer;
import no.nav.registre.skd.service.Endringskoder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("itest")
public class HodejegerConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Test
    public void shouldGetStatusQuo() {
        Endringskoder endringskode = Endringskoder.INNVANDRING;
        String miljoe = "t10";
        String fnr = "01010101010";
        stubHodejegerenHentStatusQuo(endringskode, miljoe, fnr);

        Map<String, String> response = hodejegerenConsumer.getStatusQuoFraEndringskode(endringskode, miljoe, fnr);

        assertThat(response.get("datoDo"), equalTo("01.01.01"));
    }

    // Hodejegeren henter status-quo felter på en person gitt endringskode og miljø:
    private void stubHodejegerenHentStatusQuo(Endringskoder endringskode, String miljoe, String fnr) {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/status-quo?endringskode=" + endringskode.toString() + "&miljoe=" + miljoe + "&fnr=" + fnr))
                .willReturn(okJson("{\"datoDo\": \"01.01.01\"}")));
    }
}
