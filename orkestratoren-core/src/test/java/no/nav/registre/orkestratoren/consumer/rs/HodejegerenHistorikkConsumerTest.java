package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.rtv.namespacetps.TpsPersonDokumentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenHistorikkConsumerTest {

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    private TpsPersonDokumentType tpsPersonDokument;
    private String fnr = "01010101010";

    @Before
    public void setUp() {
        tpsPersonDokument = new TpsPersonDokumentType();
    }

    @Test
    public void shouldSendPersondokumentTilHodejegeren() throws JsonProcessingException {
        stubHodejegerenConsumer();

        List<String> identer = hodejegerenHistorikkConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersonDokument, fnr);

        assertThat(identer, contains(fnr));
    }

    private void stubHodejegerenConsumer() throws JsonProcessingException {
        stubFor(post(urlPathEqualTo("/hodejegeren/api/v1/historikk/skd/oppdaterDokument/" + fnr))
                .withRequestBody(equalToJson(asJsonString(tpsPersonDokument)))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr + "\"]")));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}