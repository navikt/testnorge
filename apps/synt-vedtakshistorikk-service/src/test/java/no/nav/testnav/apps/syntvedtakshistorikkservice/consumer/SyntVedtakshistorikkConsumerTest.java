package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class SyntVedtakshistorikkConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;

    @Autowired
    private SyntVedtakshistorikkConsumer syntVedtakshistorikkConsumer;

    private void stubHistorikkResponse() {
        stubFor(post(urlPathMatching("(.*)/api/v1/vedtakshistorikk"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/synt/historikk.json")))
        );
    }

    private void stubErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/api/v1/vedtakshistorikk"))
                .willReturn(aResponse().withStatus(500))
        );
    }

    @BeforeEach
    void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Disabled
    @Test
    void shouldGetVedtakshistorikk() {
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

    @Test
    void shouldHandleErrorResponse() {
        stubErrorResponse();
        var response = syntVedtakshistorikkConsumer.syntetiserVedtakshistorikk(1);

        assertThat(response).isEmpty();
    }

}
