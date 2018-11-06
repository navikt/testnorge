package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TpsfConsumerTest {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private SendToTpsRequest sendToTpsRequest = new SendToTpsRequest("t9", Arrays.asList(123L));

    private long gruppeId = 10L;

    @Test
    public void shouldSendSkdMeldingTilTpsf() {
        stubTpsfConsumer();

        AvspillingResponse response = tpsfConsumer.sendSkdMeldingTilTpsf(gruppeId, sendToTpsRequest);
    }

    public void stubTpsfConsumer() {
        stubFor(post(urlPathEqualTo("/api/v1/endringsmelding/skd/send/" + gruppeId))
                .willReturn(ok()));
    }
}
