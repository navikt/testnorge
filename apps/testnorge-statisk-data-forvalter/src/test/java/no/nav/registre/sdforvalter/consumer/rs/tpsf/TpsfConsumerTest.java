package no.nav.registre.sdforvalter.consumer.rs.tpsf;

import no.nav.registre.sdforvalter.config.credentials.TpsfProxyProperties;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.request.SendToTpsRequest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class TpsfConsumerTest {

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private SendToTpsRequest sendToTpsRequest;

    private final long avspillergruppeId = 10L;
    private final String environment = "t9";
    private List<Long> ids;
    private int expectedAntallSendte;
    private int expectedAntallFeilet;
    private String expectedFoedselnummer;
    private Long expectedSekvensnummer;
    private String expectedStatus;

    @Before
    public void before() {
        when(tokenService.exchange(ArgumentMatchers.any(TpsfProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void shouldSendSkdMeldingTilTpsf() {
        ids = new ArrayList<>();
        ids.add(123L);
        ids.add(321L);
        sendToTpsRequest = new SendToTpsRequest(environment, ids);

        expectedAntallSendte = 1;
        expectedAntallFeilet = 0;
        expectedFoedselnummer = "01010101010";
        expectedSekvensnummer = 10L;
        expectedStatus = "ok";

        stubTpsfConsumerSendSkdMelding();

        var response = tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, sendToTpsRequest).block();

        assertThat(response).isNotNull();
        assertThat(expectedAntallSendte).isEqualTo(response.getAntallSendte());
        assertThat(expectedAntallFeilet).isEqualTo(response.getAntallFeilet());

        assertThat(expectedFoedselnummer).isEqualTo(response.getStatusFraFeilendeMeldinger().get(0).getFoedselsnummer());
        assertThat(expectedSekvensnummer).isEqualTo(response.getStatusFraFeilendeMeldinger().get(0).getSekvensnummer());
        assertThat(expectedStatus).isEqualTo(response.getStatusFraFeilendeMeldinger().get(0).getStatus());
    }


    private void stubTpsfConsumerSendSkdMelding() {
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/send/" + avspillergruppeId))
                .withRequestBody(equalToJson(
                        "{\"environment\":\"" + environment
                                + "\",\"ids\":[" + ids.get(0) + ", " + ids.get(1) + "]}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedAntallSendte
                                + "\", \"antallFeilet\": \"" + expectedAntallFeilet
                                + "\", \"statusFraFeilendeMeldinger\": [{\"foedselsnummer\": \"" + expectedFoedselnummer
                                + "\", \"sekvensnummer\": " + expectedSekvensnummer
                                + ", \"status\": \"" + expectedStatus + "\"}]}")));
    }

}
