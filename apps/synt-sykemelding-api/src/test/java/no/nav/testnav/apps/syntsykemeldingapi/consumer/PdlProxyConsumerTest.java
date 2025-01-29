package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestPdlPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class PdlProxyConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PdlProxyConsumer pdlProxyConsumer;

    private static final String IDENT = "01019049945";
    private static final String PDL_PROXY_URL = "(.*)/pdl/pdl-api/graphql";
    private PdlPerson pdlResponse;

    @BeforeEach
    void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        pdlResponse = getTestPdlPerson(IDENT);
    }

    @Test
    void shouldGetArbeidsforhold() throws JsonProcessingException {
        stubPdlProxy();

        var response = pdlProxyConsumer.getPdlPerson(IDENT);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isEmpty();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getHentPerson().getFoedsel()).isEmpty();
        assertThat(response.getData().getHentPerson().getNavn()).hasSize(1);
        assertThat(response.getData().getHentIdenter().getIdenter()).hasSize(1);
        assertThat(response.getData().getHentIdenter().getIdenter().get(0).getIdent()).isEqualTo(IDENT);
    }

    private void stubPdlProxy() throws JsonProcessingException {
        stubFor(post(urlPathMatching(PDL_PROXY_URL))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(pdlResponse))));
    }

}

