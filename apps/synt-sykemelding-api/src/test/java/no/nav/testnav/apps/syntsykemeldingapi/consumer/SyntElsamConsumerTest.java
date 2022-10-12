package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.SyntSykemeldingProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
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

import java.time.LocalDate;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestHistorikk;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class SyntElsamConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SyntElsamConsumer syntElsamConsumer;

    private static final String ident = "01019049945";
    private static final String syntUrl = "(.*)/synt/api/v1/generate_sykmeldings_history_json";
    private Map<String, SyntSykemeldingHistorikkDTO> syntResponse;

    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(SyntSykemeldingProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        syntResponse = getTestHistorikk(ident);
    }

    @Test
    public void shouldGetSyntSykemelding() throws JsonProcessingException {
        stubSynt();

        var response = syntElsamConsumer.genererSykemeldinger(ident, LocalDate.now());

        assertThat(response).isNotNull().isEqualTo(syntResponse.get(ident));
    }

    private void stubSynt() throws JsonProcessingException {
        stubFor(post(urlPathMatching(syntUrl))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(syntResponse))));
    }

}
