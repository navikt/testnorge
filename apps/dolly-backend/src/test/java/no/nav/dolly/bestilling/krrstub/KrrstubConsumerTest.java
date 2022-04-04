package no.nav.dolly.bestilling.krrstub;

import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.hamcrest.MatcherAssert;
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

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class KrrstubConsumerTest {

    private static final String EPOST = "morro.pa@landet.no";
    private static final String MOBIL = "11111111";
    private static final String IDENT = "12345678901";
    private static final boolean RESERVERT = true;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private KrrstubConsumer krrStubConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(KrrstubProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
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

        var response = krrStubConsumer.deleteKontaktdata(List.of(IDENT)).block();

        MatcherAssert.assertThat(response.size(), is(equalTo(1)));
    }

    @Test
    public void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {

        when(tokenService.exchange(any(KrrstubProxyProperties.class))).thenReturn(Mono.empty());

        Assertions.assertThrows(SecurityException.class, () -> krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                .epost(EPOST)
                .mobil(MOBIL)
                .reservert(RESERVERT)
                .build()));

        verify(tokenService).exchange(any(KrrstubProxyProperties.class));
    }

    private void stubPostKrrData() {

        stubFor(post(urlPathMatching("(.*)/api/v2/kontaktinformasjon"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubDeleteKrrData() {

        stubFor(delete(urlPathMatching("(.*)/api/v2/kontaktinformasjon/" + IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}