package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class TpsMessagingConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final List<String> MILJOER = List.of("q1", "q2");

    @MockitoBean
    @SuppressWarnings("unused")
    private AccessToken accessToken;

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private TpsMessagingConsumer tpsMessagingConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random randomKontonummer = new SecureRandom();

    @BeforeEach
    void setup() {
        when(tokenExchange.exchange(any(ServerProperties.class))).thenReturn(Mono.just(accessToken));
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

        var response = tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                        IDENT,
                        MILJOER,
                        new BankkontonrUtlandDTO())
                .collectList()
                .block();

        assertThat(response)
                .isNotNull()
                .extracting(list -> list.getFirst().getMiljoe())
                .isEqualTo("q1");
    }

    @Test
    void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {

        when(tokenExchange.exchange(any(ServerProperties.class))).thenThrow(new SecurityException());

        BankkontonrUtlandDTO bankkontonrUtlandDTO = new BankkontonrUtlandDTO();

        Assertions.assertThrows(SecurityException.class, () ->
                tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                                IDENT,
                                MILJOER,
                                bankkontonrUtlandDTO)
                        .collectList()
                        .block());

        verify(tokenExchange).exchange(any(ServerProperties.class));
    }

    @Test
    void generateUtenlandskbankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/v1/personer/(.*)/bankkonto-utenlandsk"))
                        .willReturn(ok()
                                .withFixedDelay(100)
                                .withBody("[{\"miljoe\" : \"q1\", \"status\" : \"OK\"}]")
                                .withHeader("Content-Type", "application/json")));

        List.of("1111111111", "2222222222", "333333333", "4444444444")
                .forEach(
                        p -> tpsMessagingConsumer.sendUtenlandskBankkontoRequest(p, MILJOER,
                                        BankkontonrUtlandDTO.builder()
                                                .kontonummer(Integer.toString(randomKontonummer.nextInt()))
                                                .build())
                                .collectList()
                                .block()
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
                .toList();

        var forskjelligeBankkontoer = sendtBankkontoer.stream().distinct().toList();

        AssertionsForInterfaceTypes
                .assertThat(forskjelligeBankkontoer)
                .hasSameSizeAs(sendtBankkontoer);
    }
}
