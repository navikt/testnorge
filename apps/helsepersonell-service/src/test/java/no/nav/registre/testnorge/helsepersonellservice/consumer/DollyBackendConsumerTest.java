package no.nav.registre.testnorge.helsepersonellservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.registre.testnorge.helsepersonellservice.config.credentials.DollyBackendProperties;
import no.nav.registre.testnorge.helsepersonellservice.domain.RsTestgruppeMedBestillingId;
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

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class DollyBackendConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DollyBackendConsumer dollyBackendConsumer;

    private static final String ident = "12345678910";
    private static final String dollyBackendUrl = "(.*)/dolly/api/v1/gruppe/0";

    private RsTestgruppeMedBestillingId dollyResponse;

    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(DollyBackendProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        var identBestilling = RsTestgruppeMedBestillingId.IdentBestilling.builder()
                .ident(ident)
                .build();
        dollyResponse = new RsTestgruppeMedBestillingId(Collections.singletonList(identBestilling));
    }

    @Test
    public void shouldGetGruppeIdenter() throws JsonProcessingException {
        stubDollyBackend();

        var response = dollyBackendConsumer.getHelsepersonell();

        assertThat(response).isNotNull().hasSize(1).contains(ident);
    }

    private void stubDollyBackend() throws JsonProcessingException {
        stubFor(get(urlPathMatching(dollyBackendUrl))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(dollyResponse))));
    }
}
