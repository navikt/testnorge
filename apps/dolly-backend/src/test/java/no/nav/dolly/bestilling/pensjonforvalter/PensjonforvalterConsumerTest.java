package no.nav.dolly.bestilling.pensjonforvalter;

import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
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

import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
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
                        .withBody("[\"q1\",\"q2\",\"q4\"]")
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
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"OK\"},\"message\":null,\"path\":\"/inntekt\",\"timestamp\":\"2022-06-29T00:00:00.311057793Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/inntekt"))
                    .willReturn(serverError()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"404 Not Found from GET https://tp-q4.dev.intern.nav.no/api/tjenestepensjon/000000/forhold\",\"path\":\"/tp/forhold\",\"timestamp\":\"2022-06-30T09:14:08.831669924Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    private void stubPostLagreTpForhold(boolean withError) {

        if (!withError) {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/forhold"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"No Content\"},\"message\":null,\"path\":\"/tp/forhold\",\"timestamp\":\"2022-06-30T09:16:28.094766320Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/forhold"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"POST Request failed with msg: 400 Bad Request\",\"path\":\"/inntekt\",\"timestamp\":\"2022-06-29T13:00:00.838672829Z\"}}]}")
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
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":200,\"reasonPhrase\":\"No Content\"},\"message\":null,\"path\":\"/tp/ytelse\",\"timestamp\":\"2022-06-30T09:46:31.755107085Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/ytelse"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"404 Not Found from POST https://tp-q4.dev.intern.nav.no/api/tjenestepensjon/08525803725/forhold/3200/ytelse\",\"path\":\"/tp/ytelse\",\"timestamp\":\"2022-06-30T09:47:24.835903086Z\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }

    @Test
    void testGetMiljoer() {
        stubGetMiljo();

        var miljoer = pensjonforvalterConsumer.getMiljoer();

        assertThat("There are some environments", !miljoer.isEmpty());
        assertThat("There are q1 environment", miljoer.contains("q1"));
        assertThat("There are q2 environment", miljoer.contains("q2"));
    }

    @Test
    void testOpprettPerson_ok() {
        stubPostOpprettPerson(false);

        var response = pensjonforvalterConsumer.opprettPerson(new OpprettPersonRequest(), Set.of("q1"), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 200", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(200));
    }

    @Test
    void testOppretPerson_error() {
        stubPostOpprettPerson(true);

        var response = pensjonforvalterConsumer.opprettPerson(new OpprettPersonRequest(), Set.of("q1"), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 500", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(500));
    }

    @Test
    void testLagreInntekt_ok() {

        stubPostLagreInntekt(false);

        var response = pensjonforvalterConsumer.lagreInntekter(new LagreInntektRequest(), Set.of("tx"),
                        accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 200", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(200));
    }

    @Test
    void testLagreInntekt_error() {
        stubPostLagreInntekt(true);

        var response = pensjonforvalterConsumer.lagreInntekter(new LagreInntektRequest(),
                        Set.of("tx"), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 500", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(500));
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

        var response = pensjonforvalterConsumer.lagreTpForhold(new LagreTpForholdRequest(), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 200", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(200));
    }

    @Test
    void testLagreTpForhold_error() {
        stubPostLagreTpForhold(true);

        var response = pensjonforvalterConsumer.lagreTpForhold(new LagreTpForholdRequest(), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 500", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(500));
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

        var response = pensjonforvalterConsumer.lagreTpYtelse(new LagreTpYtelseRequest(), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 200", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(200));
    }

    @Test
    void testLagreTpYtelse_error() {
        stubPostLagreTpYtelse(true);

        var response = pensjonforvalterConsumer.lagreTpYtelse(new LagreTpYtelseRequest(), accessToken)
                        .collectList()
                                .block()
                                        .get(0);

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 500", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(500));
    }
}
