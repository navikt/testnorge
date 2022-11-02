package no.nav.dolly.consumer.aareg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class AaregConsumerTest {

    private static final String IDENT = "01010101010";
    private static final String ORGNUMMER = "202020202";

    private static final String MILJOE = "t0";

    @MockBean
    private AccessToken accessToken;

    @Autowired
    private AaregConsumer aaregConsumer;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    private Arbeidsforhold opprettRequest;

    private ArbeidsforholdRespons arbeidsforholdRespons;

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    public void setUp() {
        when(aaregConsumer.getAccessToken()).thenReturn(Mono.just(accessToken));

        opprettRequest = Arbeidsforhold.builder()
                .arbeidstaker(Person.builder()
                        .offentligIdent(IDENT)
                        .build())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer(ORGNUMMER)
                        .build())
                .arbeidsavtaler(singletonList(OrdinaerArbeidsavtale.builder()
                        .yrke("121232")
                        .arbeidstidsordning("nada")
                        .build()))
                .arbeidsforholdId("1")
                .build();

        arbeidsforholdRespons = ArbeidsforholdRespons.builder()
                .miljo(MILJOE)
                .arbeidsforholdId("1")
                .build();
    }

    @Test
    public void opprettArbeidsforhold() throws JsonProcessingException {

        stubOpprettArbeidsforhold(arbeidsforholdRespons);

        var response = aaregConsumer.opprettArbeidsforhold(opprettRequest, MILJOE, accessToken)
                .collectList()
                .block();

        assertThat(response.get(0).getArbeidsforholdId(), is(equalTo("1")));
        assertThat(response.get(0).getMiljo(), is(equalTo(MILJOE)));
    }

    @Test
    public void oppdaterArbeidsforhold() throws JsonProcessingException {

        stubOppdaterArbeidsforhold(arbeidsforholdRespons);

        var response = aaregConsumer.opprettArbeidsforhold(opprettRequest, MILJOE, accessToken)
                .collectList()
                .block();

        assertThat(response.get(0).getArbeidsforholdId(), is(equalTo("1")));
        assertThat(response.get(0).getMiljo(), is(equalTo(MILJOE)));
    }

    @Test
    public void hentArbeidsforhold() throws JsonProcessingException {

        stubHentArbeidsforhold(arbeidsforholdRespons);

        var arbeidsforholdResponses = aaregConsumer.hentArbeidsforhold(IDENT, MILJOE, accessToken)
                .block();

        assertThat(arbeidsforholdResponses.getEksisterendeArbeidsforhold(), is(emptyList()));
    }

    private void stubOpprettArbeidsforhold(ArbeidsforholdRespons response) throws JsonProcessingException {

        stubFor(post(urlPathMatching("(.*)/api/v1/arbeidsforhold"))
                .willReturn(created()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubOppdaterArbeidsforhold(ArbeidsforholdRespons response) throws JsonProcessingException {

        stubFor(put(urlPathMatching("(.*)/api/v1/arbeidsforhold"))
                .willReturn(created()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubHentArbeidsforhold(ArbeidsforholdRespons response) throws JsonProcessingException {

        stubFor(get(urlPathMatching("(.*)/api/v1/arbeidsforhold"))
                .withQueryParam("ident", matching(IDENT))
                .withQueryParam("miljoe", matching(MILJOE))
                .willReturn(ok()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }
}