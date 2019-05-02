package no.nav.registre.sam.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.sam.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import no.nav.registre.sam.domain.SyntetisertSamObject;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class SamSyntetisererenConsumerTest {

    @Autowired
    private SamSyntetisererenConsumer samSyntetisererenConsumer;

    int antallMeldinger = 2;

    @Test
    public void shouldGetSyntetiserteMeldinger() {
        stubAaregSyntetisererenConsumer();

        List<SyntetisertSamObject> response = samSyntetisererenConsumer.hentSammeldingerFromSyntRest(antallMeldinger);

        assertThat(response.get(0).getDatoEndret(), equalTo("05.01.2017"));
        assertThat(response.get(0).getKSamHendelseT(), equalTo("VEDTAKNAV"));
        assertThat(response.get(1).getDatoEndret(), equalTo("13.06.2013"));
        assertThat(response.get(1).getKSamHendelseT(), equalTo("VEDTAKNAV"));
    }

    private void stubAaregSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-sam/api/v1/generate/sam?numToGenerate=" + antallMeldinger))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("samordningsmelding.json"))));
    }
}