package no.nav.dolly.bestilling.pensjonforvalter;

import no.nav.dolly.bestilling.pensjonforvalter.domain.*;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class PensjonforvalterConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private PensjonforvalterConsumer pensjonforvalterConsumer;

    @BeforeEach
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    private void stubGetMiljo() {

        stubFor(get(urlPathMatching("(.*)/api/v1/miljo"))
                .willReturn(ok()
                        .withBody("[\"q1\",\"q2\"]")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubPostOpprettPerson(boolean withError) {

        if (!withError) {
            stubFor(post(urlPathMatching("(.*)/api/v1/person"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/person\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/person"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"POST Request failed with msg: 400 Bad Request: [{\\\"message\\\":\\\"error message\\\"}]\",\"path\":\"/person\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    private void stubPostLagreInntekt(boolean withError) {

        if (!withError) {
            stubFor(post(urlPathMatching("(.*)/api/v1/inntekt"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/api/v1/inntekt\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/inntekt"))
                    .willReturn(badRequest()
                            .withBody("Feil i POPP-inntekt")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    private void stubPostLagreTpForhold(boolean withError) {

        if (!withError) {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/forhold"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/api/v1/tp/forhold\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/forhold"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"POST Request failed with msg: 400 Bad Request\",\"path\":\"/api/v1/tp/forhold\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    private void stubPostLagreTpYtelse(boolean withError) {

        if (!withError) {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/ytelse"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/api/v1/tp/ytelse\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/ytelse"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"404 Not Found from POST https://tp-q4.dev.intern.nav.no/api/tjenestepensjon/08525803725/forhold/3200/ytelse\",\"path\":\"/api/v1/tp/ytelse\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    @Test
    void testGetMiljoer() {

        stubGetMiljo();

        StepVerifier.create(pensjonforvalterConsumer.getMiljoer())
                .expectNext(Set.of("q1", "q2"))
                .verifyComplete();
    }

    @Test
    void testOpprettPerson_ok() {

        stubPostOpprettPerson(false);

        StepVerifier.create(pensjonforvalterConsumer
                        .opprettPerson(new PensjonPersonRequest(), Set.of("tx")))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/person")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testOpprettPerson_error() {

        stubPostOpprettPerson(true);

        StepVerifier.create(pensjonforvalterConsumer.opprettPerson(new PensjonPersonRequest(), Set.of("q1")))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(500)
                                                .reasonPhrase("Internal Server Error")
                                                .build())
                                        .path("/person")
                                        .message("POST Request failed with msg: 400 Bad Request: [{\"message\":\"error message\"}]")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreInntekt_ok() {

        stubPostLagreInntekt(false);

        StepVerifier.create(pensjonforvalterConsumer.lagreInntekter(new PensjonPoppInntektRequest()))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/api/v1/inntekt")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreInntekt_error() {

        stubPostLagreInntekt(true);

        StepVerifier.create(pensjonforvalterConsumer.lagreInntekter(PensjonPoppInntektRequest.builder()
                        .miljoer(List.of("tx"))
                        .build()))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(400)
                                                .reasonPhrase("Bad Request")
                                                .build())
                                        .path("/api/v1/inntekt")
                                        .message("Feil i POPP-inntekt")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_ok() {

        stubPostLagreTpForhold(false);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpForhold(new PensjonTpForholdRequest()))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/api/v1/tp/forhold")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_error() {

        stubPostLagreTpForhold(true);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpForhold(new PensjonTpForholdRequest()))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(500)
                                                .reasonPhrase("Internal Server Error")
                                                .build())
                                        .path("/api/v1/tp/forhold")
                                        .message("POST Request failed with msg: 400 Bad Request")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreTpYtelse_ok() {

        stubPostLagreTpYtelse(false);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpYtelse(new PensjonTpYtelseRequest()))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/api/v1/tp/ytelse")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreTpYtelse_error() {

        stubPostLagreTpYtelse(true);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpYtelse(new PensjonTpYtelseRequest()))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(500)
                                                .reasonPhrase("Internal Server Error")
                                                .build())
                                        .path("/api/v1/tp/ytelse")
                                        .message("404 Not Found from POST https://tp-q4.dev.intern.nav.no/api/tjenestepensjon/08525803725/forhold/3200/ytelse")
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }
}
