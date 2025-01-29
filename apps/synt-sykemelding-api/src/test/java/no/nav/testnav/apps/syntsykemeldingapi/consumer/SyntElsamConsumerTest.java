package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.libs.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestHistorikk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class SyntElsamConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SyntElsamConsumer syntElsamConsumer;

    private static final String IDENT = "01019049945";
    private static final String SYNT_URL = "(.*)/synt/api/v1/generate_sykmeldings_history_json";
    private Map<String, SyntSykemeldingHistorikkDTO> syntResponse;

    @BeforeEach
    void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        syntResponse = getTestHistorikk(IDENT);
    }

    @Test
    void shouldGetSyntSykemelding() throws JsonProcessingException {
        stubSynt();

        var response = syntElsamConsumer.genererSykemeldinger(IDENT, LocalDate.now());

        assertThat(response).isNotNull().isEqualTo(syntResponse.get(IDENT));
    }

    private void stubSynt() throws JsonProcessingException {
        stubFor(post(urlPathMatching(SYNT_URL))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(syntResponse))));
    }

}
