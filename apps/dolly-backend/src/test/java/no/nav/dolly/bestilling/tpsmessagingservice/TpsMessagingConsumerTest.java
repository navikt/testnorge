package no.nav.dolly.bestilling.tpsmessagingservice;

import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.UtenlandskBankkonto;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.UtenlandskBankkontoRequest;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
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
public class TpsMessagingConsumerTest {

    private static final String IDENT = "12345678901";
    private static final List<String> MILJOER = List.of("q1", "q2");

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private TpsMessagingConsumer tpsMessagingConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.generateToken(ArgumentMatchers.any(TpsMessagingServiceProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void createUtenlandskbankkonto_OK() {

        stubPostUtenlandskBankkontoData();

        ResponseEntity<Object> response = tpsMessagingConsumer.sendUtenlandskBankkontoRequest(new UtenlandskBankkontoRequest(
                IDENT,
                MILJOER,
                UtenlandskBankkonto.builder().build()

        ));

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {

        when(tokenService.generateToken(any(TpsMessagingServiceProperties.class))).thenReturn(Mono.empty());

        Assertions.assertThrows(SecurityException.class, () -> tpsMessagingConsumer.sendUtenlandskBankkontoRequest(new UtenlandskBankkontoRequest(
                IDENT,
                MILJOER,
                UtenlandskBankkonto.builder().build()
        )));

        verify(tokenService).generateToken(any(TpsMessagingServiceProperties.class));
    }

    private void stubPostUtenlandskBankkontoData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/personer/12345678901/bankkonto-utenlandsk"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}