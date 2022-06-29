package no.nav.dolly.bestilling.pensjonforvalter;

import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class PensjonforvalterConsumerTest {
    private static final String MILJOER_HENT_TILGJENGELIGE_URL = "/api/v1/miljo";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

//    @MockBean
//    private PensjonforvalterClient pensjonforvalterClient;

    @Autowired
    private PensjonforvalterConsumer pensjonforvalterConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(PensjonforvalterProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    private void stubGetMiljo() {

        stubFor(get(urlPathMatching("(.*)/api/v1/miljo"))
                .willReturn(ok()
                        .withBody("[\"q1\",\"q2\",\"q4\"]")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubPostOppretPerson(boolean withError) {

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

    @Test
    public void testGetMiljoer() {
        stubGetMiljo();

        var miljoer = pensjonforvalterConsumer.getMiljoer();

        assertThat("There are some environments", !miljoer.isEmpty());
        assertThat("There are q1 environment", miljoer.contains("q1"));
        assertThat("There are q2 environment", miljoer.contains("q2"));
        assertThat("There are q4 environment", miljoer.contains("q4"));
    }

    @Test
    public void testOppretPerson_ok() {
        stubPostOppretPerson(false);

        var response = pensjonforvalterConsumer.opprettPerson(new OpprettPersonRequest());

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 200", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(200));
    }

    @Test
    public void testOppretPerson_error() {
        stubPostOppretPerson(true);

        var response = pensjonforvalterConsumer.opprettPerson(new OpprettPersonRequest());

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 500", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(500));
    }

    @Test
    public void testLagreInntekt_ok() {
        stubPostLagreInntekt(false);

        var response = pensjonforvalterConsumer.lagreInntekt(new LagreInntektRequest());

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 200", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(200));
    }

    @Test
    public void testLagreInntekt_error() {
        stubPostLagreInntekt(true);

        var response = pensjonforvalterConsumer.lagreInntekt(new LagreInntektRequest());

        assertThat("There is status for 1 environment", response.getStatus().size() == 1);
        assertThat("Environment is 'tx'", response.getStatus().get(0).getMiljo().equals("tx"));
        assertThat("Http status is 500", response.getStatus().get(0).getResponse().getHttpStatus().getStatus().equals(500));
    }

    @Test
    public void testGetInntekter() {
        stubGetGetInntekter();

        var response = pensjonforvalterConsumer.getInntekter(null, null);

        assertThat("Environment is 'tx'", response.get("miljo").asText().equals("tx"));
        assertThat("Fnr is '00000'", response.get("fnr").asText().equals("00000"));
        assertThat("inntekter is array", response.get("inntekter").isArray());
        assertThat("inntekter have 1 object", response.get("inntekter").size() == 1);
        assertThat("inntekter 'belop' is 12345", response.get("inntekter").get(0).get("belop").asInt() == 12345);
        assertThat("inntekter 'inntektAar' is 2000", response.get("inntekter").get(0).get("inntektAar").asInt() == 2000);
    }
}
