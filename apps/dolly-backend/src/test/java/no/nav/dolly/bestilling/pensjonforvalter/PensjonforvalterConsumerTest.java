package no.nav.dolly.bestilling.pensjonforvalter;

import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.config.credentials.PensjonforvalterProxyProperties;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
class PensjonforvalterConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private AccessToken accessToken;

    @Autowired
    private PensjonforvalterConsumer pensjonforvalterConsumer;

    @BeforeEach
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(PensjonforvalterProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
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
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/person\",\"timestamp\":\"2022-06-29T00:00:00.311057793Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/person"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"POST Request failed with msg: 400 Bad Request: [{\\\"message\\\":\\\"error message\\\"}]\",\"path\":\"/person\",\"timestamp\":\"2022-06-29T13:00:00.838672829Z\"}}]}")
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
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/api/v1/tp/forhold\",\"timestamp\":\"2022-06-30T09:16:00\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/forhold"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"POST Request failed with msg: 400 Bad Request\",\"path\":\"/api/v1/tp/forhold\",\"timestamp\":\"2022-06-29T13:00:00.838672829Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    private void stubGetGetInntekter() {

        stubFor(get(urlPathMatching("(.*)/api/v1/inntekt"))
                .willReturn(ok()
                        .withBody("{\"miljo\":\"tx\",\"fnr\":\"00000\",\"inntekter\":[{\"belop\":12345,\"inntektAar\":2000}]}")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubGetGetTpForhold() {
        stubFor(get(urlPathMatching("(.*)/api/v1/tp/forhold"))
                .willReturn(ok()
                        .withBody("[{\"ordning\":\"0001\"},{\"ordning\":\"0002\"},{\"ordning\":\"0003\"},{\"ordning\":\"0004\"}]")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubPostLagreTpYtelse(boolean withError) {

        if (!withError) {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/ytelse"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/api/v1/tp/ytelse\",\"timestamp\":\"2022-06-30T09:46:00\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/ytelse"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"404 Not Found from POST https://tp-q4.dev.intern.nav.no/api/tjenestepensjon/08525803725/forhold/3200/ytelse\",\"path\":\"/api/v1/tp/ytelse\",\"timestamp\":\"2022-06-30T09:47:00\"}}]}")
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
                        .opprettPerson(new OpprettPersonRequest(), Set.of("tx"), accessToken))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/person")
                                        .timestamp(LocalDateTime.of(2022, 6, 29, 00, 00))
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testOpprettPerson_error() {

        stubPostOpprettPerson(true);

        StepVerifier.create(pensjonforvalterConsumer.opprettPerson(new OpprettPersonRequest(), Set.of("q1"), accessToken))
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
                                        .timestamp(LocalDateTime.of(2022, 6, 29, 13, 00))
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreInntekt_ok() {

        stubPostLagreInntekt(false);

        StepVerifier.create(pensjonforvalterConsumer.lagreInntekter(new LagreInntektRequest(), Set.of("tx"), accessToken))
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

        StepVerifier.create(pensjonforvalterConsumer.lagreInntekter(new LagreInntektRequest(), Set.of("tx"), accessToken))
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
    void testGetInntekter() {

        stubGetGetInntekter();

        var response = pensjonforvalterConsumer.getInntekter(null, null);

        assertThat("Environment is 'tx'", response.get("miljo").asText().equals("tx"));
        assertThat("Fnr is '00000'", response.get("fnr").asText().equals("00000"));
        assertThat("inntekter is array", response.get("inntekter").isArray());
        assertThat("inntekter have 1 object", response.get("inntekter").size() == 1);
        assertThat("inntekter 'belop' is 12345", response.get("inntekter").get(0).get("belop").asInt() == 12345);
        assertThat("inntekter 'inntektAar' is 2000", response.get("inntekter").get(0).get("inntektAar").asInt() == 2000);
    }

    @Test
    void testLagreTpForhold_ok() {

        stubPostLagreTpForhold(false);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpForhold(new LagreTpForholdRequest(), accessToken))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/api/v1/tp/forhold")
                                        .timestamp(LocalDateTime.of(2022,6,30,9,16))
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_error() {

        stubPostLagreTpForhold(true);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpForhold(new LagreTpForholdRequest(), accessToken))
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
                                        .timestamp(LocalDateTime.of(2022,6,29,13, 0))
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testGetTpForhold() {
        stubGetGetTpForhold();

        var response = pensjonforvalterConsumer.getTpForhold(null, null);

        assertThat("size of response list is 4", response.size() == 4);

        assertThat("ordning 1 is '0001'", response.get(0).get("ordning").asText().equals("0001"));
        assertThat("ordning 2 is '0002'", response.get(1).get("ordning").asText().equals("0002"));
        assertThat("ordning 3 is '0003'", response.get(2).get("ordning").asText().equals("0003"));
        assertThat("ordning 4 is '0004'", response.get(3).get("ordning").asText().equals("0004"));
    }

    @Test
    void testLagreTpYtelse_ok() {

        stubPostLagreTpYtelse(false);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpYtelse(new LagreTpYtelseRequest(), accessToken))
                .expectNext(PensjonforvalterResponse.builder()
                        .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo("tx")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .reasonPhrase("OK")
                                                .build())
                                        .path("/api/v1/tp/ytelse")
                                        .timestamp(LocalDateTime.of(2022,6,30,9,46))
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void testLagreTpYtelse_error() {

        stubPostLagreTpYtelse(true);

        StepVerifier.create(pensjonforvalterConsumer.lagreTpYtelse(new LagreTpYtelseRequest(), accessToken))
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
                                        .timestamp(LocalDateTime.of(2022,6,30,9,47))
                                        .build())
                                .build()))
                        .build())
                .verifyComplete();
    }
}
