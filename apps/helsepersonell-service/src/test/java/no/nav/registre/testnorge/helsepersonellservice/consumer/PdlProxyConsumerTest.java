package no.nav.registre.testnorge.helsepersonellservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.registre.testnorge.helsepersonellservice.config.credentials.PdlProxyProperties;
import no.nav.registre.testnorge.helsepersonellservice.domain.PdlPersonBolk;
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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


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

    private static final String ident = "12345678910";
    private static final String pdlProxyUrl = "(.*)/pdl/pdl-api/graphql";

    private PdlPersonBolk pdlResponse;

    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(PdlProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        var metadata = new PdlPersonBolk.Metadata(false);
        var navn = new PdlPersonBolk.Navn("Hans", "Hans", "Hansen", metadata);
        var person = new PdlPersonBolk.Person(Collections.singletonList(navn));
        var personBolk = new PdlPersonBolk.PersonBolk(ident, person);
        var data = new PdlPersonBolk.Data(Collections.singletonList(personBolk), Collections.emptyList());
        pdlResponse = new PdlPersonBolk(data);
    }

    @Test
    public void shouldGetPdlBolk() throws JsonProcessingException {
        stubPdlProxy();

        var response = pdlProxyConsumer.getPdlPersoner(Collections.singletonList(ident));

        assertThat(response).isNotNull().isEqualTo(pdlResponse);
    }

    private void stubPdlProxy() throws JsonProcessingException {
        stubFor(post(urlPathMatching(pdlProxyUrl))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(pdlResponse))));
    }

}
