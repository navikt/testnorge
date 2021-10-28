package no.nav.dolly.bestilling.krrstub;

import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class KrrstubConsumerTest {

    private static final String EPOST = "morro.pa@landet.no";
    private static final String MOBIL = "11111111";
    private static final Long IDENT = 12345678901L;
    private static final boolean RESERVERT = true;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private KrrstubConsumer krrStubConsumer;

    @Before
    public void setup() {

        when(tokenService.generateToken(ArgumentMatchers.any(KrrstubProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void createDigitalKontaktdata_Ok() {

        stubPostKrrData();

        ResponseEntity<Object> response = krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                .epost(EPOST)
                .mobil(MOBIL)
                .reservert(RESERVERT)
                .build());

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void deleteDigitalKontaktdata_Ok() {

        stubDeleteKrrData();

        ResponseEntity<Object> response = krrStubConsumer.deleteDigitalKontaktdata(IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test(expected = DollyFunctionalException.class)
    public void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {

        when(tokenService.generateToken(any(KrrstubProxyProperties.class))).thenReturn(Mono.empty());

        krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                .epost(EPOST)
                .mobil(MOBIL)
                .reservert(RESERVERT)
                .build());

        verify(tokenService).generateToken(any(KrrstubProxyProperties.class));
    }

    private void stubPostKrrData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/kontaktinformasjon"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubDeleteKrrData() {

        stubFor(delete(urlPathMatching("(.*)/api/v1/kontaktinformasjon/" + IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}