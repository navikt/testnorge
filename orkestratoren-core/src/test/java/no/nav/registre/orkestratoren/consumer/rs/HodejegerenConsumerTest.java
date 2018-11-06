package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private long gruppeId = 10L;

    @Test
    public void shouldStartSyntetisering() {
        stubHodejegerenConsumer();

        HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        GenereringsOrdreRequest ordreRequest = new GenereringsOrdreRequest(gruppeId, "t9", antallMeldingerPerAarsakskode);

        List<Long> ids = hodejegerenConsumer.startSyntetisering(ordreRequest);
    }

    public void stubHodejegerenConsumer() {
        stubFor(post(urlPathEqualTo("/api/v1/syntetisering/generer"))
                .willReturn(ok()));
    }
}
