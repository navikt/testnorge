package no.nav.registre.inst.consumer.rs;

import org.junit.Test;
import org.junit.runner.RunWith;
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
import static no.nav.registre.inst.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class InstSyntetisererenConsumerTest {

    @Autowired
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    private int numToGenerate = 2;

    @Test
    public void shouldGetMeldinger() {
        stubInstSyntetisererenConsumer();
        var result = instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(numToGenerate);

        assertThat(result.size(), is(numToGenerate));
        assertThat(result.get(0).getTssEksternId(), equalTo("440"));
        assertThat(result.get(1).getTssEksternId(), equalTo("441"));
    }

    private void stubInstSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-inst/api/v1/generate/inst?numToGenerate=" + numToGenerate))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmelding.json"))));
    }
}