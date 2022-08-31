package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.HelsepersonellServiceProperties;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestLegeListeDTO;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class HelsepersonellConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HelsepersonellConsumer helsepersonellConsumer;

    private static final String helsepersonellUrl = "(.*)/testnav-helsepersonell/api/v1/helsepersonell";

    private HelsepersonellListeDTO helsepersonellResponse;

    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(HelsepersonellServiceProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        helsepersonellResponse = getTestLegeListeDTO();
    }

    @Test
    public void shouldGetHelsepersonell() throws JsonProcessingException {
        stubHelsepersonell();

        var response = helsepersonellConsumer.hentHelsepersonell();

        assertThat(response).isNotNull();
        assertThat(response.getList()).isNotNull().hasSize(1);
        assertThat(response.getList().get(0).getIdent()).isNotNull().isEqualTo(helsepersonellResponse.getHelsepersonell().get(0).getFnr());
    }

    private void stubHelsepersonell() throws JsonProcessingException {
        stubFor(get(urlPathMatching(helsepersonellUrl))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(helsepersonellResponse))));
    }

}
