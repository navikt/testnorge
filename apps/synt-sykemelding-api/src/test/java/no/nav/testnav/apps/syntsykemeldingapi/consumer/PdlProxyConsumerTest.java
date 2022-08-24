package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.PdlProxyProperties;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestPdlPerson;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class PdlProxyConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PdlProxyConsumer pdlProxyConsumer;

    private static final String ident = "01019049945";
    private static final String pdlProxyUrl = "(.*)/pdl/pdl-api/graphql";
    private PdlPerson pdlResponse;

    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(PdlProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        pdlResponse = getTestPdlPerson(ident);
    }

    @Test
    public void shouldGetArbeidsforhold() throws JsonProcessingException {
        stubPdlProxy();

        var response = pdlProxyConsumer.getPdlPerson(ident);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isEmpty();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getHentPerson().getFoedsel()).isEmpty();
        assertThat(response.getData().getHentPerson().getNavn()).hasSize(1);
        assertThat(response.getData().getHentIdenter().getIdenter()).hasSize(1);
        assertThat(response.getData().getHentIdenter().getIdenter().get(0).getIdent()).isEqualTo(ident);
    }

    private void stubPdlProxy() throws JsonProcessingException {
        stubFor(post(urlPathMatching(pdlProxyUrl))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(pdlResponse))));
    }

}

