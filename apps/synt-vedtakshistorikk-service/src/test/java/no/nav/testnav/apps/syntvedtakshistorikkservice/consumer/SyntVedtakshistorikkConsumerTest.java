package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.SyntVedtakshistorikkProperties;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class SyntVedtakshistorikkConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private SyntVedtakshistorikkConsumer syntVedtakshistorikkConsumer;

    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(SyntVedtakshistorikkProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void shoudlGetVedtakshistorikk(){
        stubHistorikkResponse();
        var response = syntVedtakshistorikkConsumer.syntetiserVedtakshistorikk(2);

        assertThat(response).hasSize(2);

        var historikk1 = response.get(0);
        assertThat(historikk1.getAap115()).hasSize(1);
        assertThat(historikk1.getAap()).hasSize(5);
        assertThat(historikk1.getAlleVedtak()).hasSize(6);
        assertThat(historikk1.getAlleTilleggVedtak()).isEmpty();
        assertThat(historikk1.getAlleTiltakVedtak()).isEmpty();

        var historikk2 = response.get(1);
        assertThat(historikk2.getAap115()).hasSize(1);
        assertThat(historikk2.getAap()).hasSize(4);
        assertThat(historikk2.getAlleVedtak()).hasSize(5);
        assertThat(historikk2.getAlleTilleggVedtak()).isEmpty();
        assertThat(historikk2.getAlleTiltakVedtak()).isEmpty();
    }

    private void stubHistorikkResponse() {
        stubFor(post(urlPathMatching("(.*)/synt/api/v1/vedtakshistorikk"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/synt/historikk.json")))
        );
    }

    @Test
    void shouldHandleErrorResponse(){
        stubErrorResponse();
        var response = syntVedtakshistorikkConsumer.syntetiserVedtakshistorikk(1);

        assertThat(response).isEmpty();
    }

    private void stubErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/synt/api/v1/vedtakshistorikk"))
                .willReturn(aResponse().withStatus(500))
        );
    }
}
