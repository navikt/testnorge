package no.nav.dolly.bestilling.tpsmessagingservice;

import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsUtenlandskBankkonto;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.TpsMessagingResponse;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.UtenlandskBankkontoRequest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Assertions;
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

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
class TpsMessagingConsumerTest {

    private static final String IDENT = "12345678901";
    private static final List<String> MILJOER = List.of("q1", "q2");

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private TpsMessagingConsumer tpsMessagingConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(TpsMessagingServiceProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    private void stubPostUtenlandskBankkontoData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/personer/12345678901/bankkonto-utenlandsk"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    @Test
    void createUtenlandskbankkonto_OK() {

        stubPostUtenlandskBankkontoData();

        ResponseEntity<TpsMessagingResponse> response = tpsMessagingConsumer.sendUtenlandskBankkontoRequest(new UtenlandskBankkontoRequest(
                IDENT,
                MILJOER,
                new RsUtenlandskBankkonto()

        ));

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {

        when(tokenService.exchange(any(TpsMessagingServiceProperties.class))).thenReturn(Mono.empty());
        UtenlandskBankkontoRequest utenlandskBankkontoRequest = new UtenlandskBankkontoRequest(
                IDENT,
                MILJOER,
                RsUtenlandskBankkonto.builder().build()
        );
        Assertions.assertThrows(SecurityException.class, () -> tpsMessagingConsumer.sendUtenlandskBankkontoRequest(utenlandskBankkontoRequest));

        verify(tokenService).exchange(any(TpsMessagingServiceProperties.class));
    }
}