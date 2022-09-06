package no.nav.registre.testnorge.helsepersonellservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.registre.testnorge.helsepersonellservice.config.credentials.SamhandlerregisteretServerProperties;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.IdentDTO;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
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
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class SamhandlerregisteretConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SamhandlerregisteretConsumer samhandlerregisteretConsumer;

    private static final String ident = "12345678910";
    private static final String samrUrl = "(.*)/samhandler/rest/sar/samh";
    private SamhandlerDTO[] samhandlerResponse;


    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(SamhandlerregisteretServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        var samhandler = SamhandlerDTO.builder()
                .identer(Collections.singletonList(IdentDTO.builder()
                                .ident(ident)
                                .identTypeKode("FNR")
                        .build()))
                .kode("LE")
                .build();
        samhandlerResponse = new SamhandlerDTO[]{samhandler};
    }

    @Test
    public void shouldGetSamhandler() throws JsonProcessingException, ExecutionException, InterruptedException {
        stubSamhandlerregisteret();

        var response = samhandlerregisteretConsumer.getSamhandler(ident).get();

        assertThat(response).isNotNull().hasSize(1);
        assertThat(response.get(0).getIdent()).isEqualTo(ident);
    }


    private void stubSamhandlerregisteret() throws JsonProcessingException {
        stubFor(get(urlPathMatching(samrUrl))
                .withQueryParam("ident", equalTo(ident))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(samhandlerResponse))));
    }

}
