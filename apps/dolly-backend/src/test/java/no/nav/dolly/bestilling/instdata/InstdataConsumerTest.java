package no.nav.dolly.bestilling.instdata;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.config.credentials.InstProxyProperties;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class InstdataConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENVIRONMENT = "U2";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Autowired
    private InstdataConsumer instdataConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(InstProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void deleteInstdata() {

        stubDeleteInstData();

        instdataConsumer.deleteInstdata(IDENT, ENVIRONMENT);

        verify(tokenService).exchange(ArgumentMatchers.any(InstProxyProperties.class));
    }

    @Test
    public void postInstdata() {

        stubPostInstData();

        ResponseEntity<List<InstdataResponse>> response = instdataConsumer.postInstdata(singletonList(Instdata.builder().build()), ENVIRONMENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    private void stubPostInstData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/ident/batch"))
                .withQueryParam("miljoe", equalTo("U2"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubDeleteInstData() {

        stubFor(delete(urlPathMatching("(.*)/api/v1/ident/batch"))
                .withQueryParam("identer", equalTo(IDENT))
                .withQueryParam("miljoe", equalTo(ENVIRONMENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}