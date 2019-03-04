package no.nav.registre.inst.consumer.rs.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.inst.consumer.rs.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class InstSyntetisererenConsumerTest {

    @Autowired
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    private int numToGenerate = 2;

    @Test
    public void shouldGetMeldinger() {
        stubInstSyntetisererenConsumer();
        List<Map<String, String>> result = instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(numToGenerate);

        assertThat(result.size(), is(numToGenerate));
        assertThat(result.get(0).get("endret_av"), equalTo("L113794"));
        assertThat(result.get(1).get("endret_av"), equalTo("KONVERTERT_FRA_IS20"));
    }

    public void stubInstSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-inst/api/v1/generate/inst?numToGenerate=" + numToGenerate))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmelding.json"))));
    }
}