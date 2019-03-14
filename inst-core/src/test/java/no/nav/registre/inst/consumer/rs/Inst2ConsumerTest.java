package no.nav.registre.inst.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class Inst2ConsumerTest {

    @Autowired
    private Inst2Consumer inst2Consumer;

    @Test
    public void shouldGetTokenForInst2() {
        stubTokenProvider();

        inst2Consumer.hentTokenTilInst2();
    }

    public void stubTokenProvider() {
        stubFor(get(urlPathEqualTo("/freg-token-provider/token/user"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("username", equalTo("dummy"))
                .withHeader("password", equalTo("dummy"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));
    }

}