package no.nav.dolly.bestilling.tpsmessagingservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TpsMessagingConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final List<String> MILJOER = List.of("q1", "q2");
    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final Random randomKontonummer = new SecureRandom();
    @Autowired
    private TpsMessagingConsumer tpsMessagingConsumer;

    private void stubPostUtenlandskBankkontoData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/personer/12345678901/bankkonto-utenlandsk"))
                .willReturn(ok()
                        .withBody("[{\"miljoe\" : \"q1\", \"status\" : \"OK\"}]")
                        .withHeader("Content-Type", "application/json")));
    }

    @Test
    void createUtenlandskbankkonto_OK() {

        stubPostUtenlandskBankkontoData();

        tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                        IDENT,
                        MILJOER,
                        new BankkontonrUtlandDTO())
                .collectList()
                .as(StepVerifier::create)
                .assertNext(response -> assertThat(response)
                        .isNotNull()
                        .extracting(list -> list.getFirst().getStatus())
                        .isEqualTo("OK"))
                .verifyComplete();
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
                                .as(StepVerifier::create)
                                .assertNext(response ->
                                        assertThat(response)
                                                .isNotNull()
                                                .extracting(list -> list.getFirst().getStatus())
                                                .isEqualTo("OK")
                                )
                                .verifyComplete()
                );

        var sendtBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> objectMapper.readValue(s, BankkontonrUtlandDTO.class))
                .toList();

        var forskjelligeBankkontoer = sendtBankkontoer.stream().distinct().toList();

        AssertionsForInterfaceTypes
                .assertThat(forskjelligeBankkontoer)
                .hasSameSizeAs(sendtBankkontoer);
    }

}
