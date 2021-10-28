package no.nav.dolly.consumer.aareg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.config.credentials.TestnorgeAaregProxyProperties;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class AaregConsumerTest {

    private final String ident = "01010101010";
    private final String miljoe = "t0";
    @Autowired
    private AaregConsumer aaregConsumer;
    @MockBean
    private TokenExchange tokenService;
    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;
    private AaregOpprettRequest opprettRequest;
    private AaregResponse opprettResponse;
    private AaregResponse slettResponse;

    @BeforeEach
    public void setUp() {
        when(tokenService.generateToken(ArgumentMatchers.any(TestnorgeAaregProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        opprettRequest = AaregOpprettRequest.builder()
                .arbeidsforhold(Arbeidsforhold.builder()
                        .build())
                .environments(Collections.singletonList(miljoe))
                .build();
        Map<String, String> status = new HashMap<>();
        status.put(miljoe, "OK");
        opprettResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        slettResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();
    }

    @Test
    public void opprettArbeidsforhold() throws JsonProcessingException {
        stubOpprettArbeidsforhold(opprettResponse);

        AaregResponse response = aaregConsumer.opprettArbeidsforhold(opprettRequest);

        assertThat(response.getStatusPerMiljoe().get(miljoe), equalTo("OK"));
    }

    @Test
    public void hentArbeidsforhold() {
        stubHentArbeidsforhold();

        List<ArbeidsforholdResponse> arbeidsforholdResponses = aaregConsumer.hentArbeidsforhold(ident, miljoe);

        assertThat(arbeidsforholdResponses.get(0), notNullValue());
    }

    @Test
    public void slettArbeidsforhold() throws JsonProcessingException {
        stubSlettIdentFraAlleMiljoer(slettResponse);

        AaregResponse response = aaregConsumer.slettArbeidsforholdFraAlleMiljoer(ident);

        assertThat(response.getStatusPerMiljoe().get(miljoe), equalTo("OK"));
    }

    private void stubOpprettArbeidsforhold(AaregResponse response) throws JsonProcessingException {

        stubFor(post(urlPathMatching("(.*)/aareg/api/v1/arbeidsforhold"))
                .willReturn(created()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubHentArbeidsforhold() {

        stubFor(get(urlPathMatching("(.*)/aareg/api/v1/arbeidsforhold"))
                .withQueryParam("ident", matching(ident))
                .withQueryParam("miljoe", matching(miljoe))
                .willReturn(ok()
                        .withBody("[{}]")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubSlettIdentFraAlleMiljoer(AaregResponse response) throws JsonProcessingException {

        stubFor(delete(urlPathMatching("(.*)/aareg/api/v1/arbeidsforhold"))
                .withQueryParam("ident", matching(ident))
                .willReturn(ok()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}