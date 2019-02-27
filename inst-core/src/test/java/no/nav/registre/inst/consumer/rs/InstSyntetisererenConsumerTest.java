package no.nav.registre.inst.consumer.rs;

import com.github.tomakehurst.wiremock.common.Json;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class InstSyntetisererenConsumerTest {

    @Autowired
    InstSyntetisererenConsumer instSyntetisererenConsumer;

    @Test
    public void shouldGetMeldinger() {
        stubPoppSyntetisererenConsumer();
        List<Map<String, String>> result = instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(2);
        }

    public void stubPoppSyntetisererenConsumer() {}
}