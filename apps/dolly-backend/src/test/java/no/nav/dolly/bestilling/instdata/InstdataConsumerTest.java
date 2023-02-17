package no.nav.dolly.bestilling.instdata;

import no.nav.dolly.config.credentials.InstServiceProperties;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
class InstdataConsumerTest {

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
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(InstServiceProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void deleteInstdata() {

        stubDeleteInstData();

        instdataConsumer.deleteInstdata(List.of(IDENT))
                        .subscribe(resultat ->
                                verify(tokenService).exchange(ArgumentMatchers.any(InstServiceProperties.class)));
    }

    @Test
    void postInstdata() {

        stubPostInstData();

        StepVerifier.create(instdataConsumer.postInstdata(singletonList(Instdata.builder().build()), ENVIRONMENT))
                .expectNextCount(1)
                .verifyComplete();
    }

    private void stubPostInstData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/institusjonsopphold/person"))
                .withQueryParam("miljoe", equalTo("U2"))
                .willReturn(ok()));
    }

    private void stubDeleteInstData() {

        stubFor(delete(urlPathMatching("(.*)/api/v1/ident/batch"))
                .withQueryParam("identer", equalTo(IDENT))
                .withQueryParam("miljoe", equalTo(ENVIRONMENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlPathMatching("(.*)/api/v1/miljoer"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + ENVIRONMENT + "\"]")));
    }
}