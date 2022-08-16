package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Assertions;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(TpsMessagingServiceProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    private void stubPostUtenlandskBankkontoData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/personer/12345678901/bankkonto-utenlandsk"))
                .willReturn(ok()
                        .withBody("[{\"miljoe\" : \"q1\", \"status\" : \"OK\"}]")
                        .withHeader("Content-Type", "application/json")));
    }

    @Test
    void createUtenlandskbankkonto_OK() {

        stubPostUtenlandskBankkontoData();

        List<TpsMeldingResponseDTO> response = tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                IDENT,
                MILJOER,
                new BankkontonrUtlandDTO());

        assertThat("First env should be q1", response.get(0).getMiljoe().equals("q1"));
    }

    @Test
    void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {

        when(tokenService.exchange(any(TpsMessagingServiceProperties.class))).thenReturn(Mono.empty());

        BankkontonrUtlandDTO bankkontonrUtlandDTO = new BankkontonrUtlandDTO();

        Assertions.assertThrows(SecurityException.class, () -> tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                IDENT,
                MILJOER,
                bankkontonrUtlandDTO));

        verify(tokenService).exchange(any(TpsMessagingServiceProperties.class));
    }

    @Test
    void generateUtenlandskbankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/v1/personer/(.*)/bankkonto-utenlandsk"))
                        .willReturn(ok()
                                .withFixedDelay(100)
                                .withBody("[{\"miljoe\" : \"q1\", \"status\" : \"OK\"}]")
                                .withHeader("Content-Type", "application/json")));

        var dto = new BankkontonrUtlandDTO();
        dto.setTilfeldigKontonummer(true);

        List.of("1111111111", "2222222222", "333333333", "4444444444")
                .parallelStream()
                .forEach(
                        p -> tpsMessagingConsumer.sendUtenlandskBankkontoRequest(p, MILJOER, dto)
                );

        var sendtBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, BankkontonrUtlandDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        var forskjelligeBankkontoer = sendtBankkontoer.stream().distinct().collect(Collectors.toList());

        assertThat("tilfeldig kontonummer is True", dto.getTilfeldigKontonummer());
        assertThat("sendt forskjellige kontonummer", forskjelligeBankkontoer.size() == sendtBankkontoer.size());
    }

    @Test
    void generateDifferentBankkonto() {
        var kontoer = IntStream.range(1, 100).boxed()
                .map((i) -> TpsMessagingConsumer.tilfeldigUtlandskBankkonto(KontoregisterLandkode.SE.name()))
                .sorted()
                .collect(Collectors.toList());

        var unikKontoer = kontoer.stream().distinct().count();

        assertThat("forskjellige kontoer", unikKontoer == kontoer.size());
    }

    @Test
    void generateBankkontoWithUknownLandkode() {
        var konto = TpsMessagingConsumer.tilfeldigUtlandskBankkonto("AABB");
        assertThat("konto har data", !konto.isEmpty());
    }
}
