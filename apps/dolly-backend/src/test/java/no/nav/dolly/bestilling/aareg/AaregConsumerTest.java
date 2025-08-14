package no.nav.dolly.bestilling.aareg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.util.List;

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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class AaregConsumerTest {

    private static final String IDENT = "01010101010";
    private static final String ORGNUMMER = "202020202";

    private static final String MILJOE = "q2";

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @Autowired
    private AaregConsumer aaregConsumer;

    private Arbeidsforhold opprettRequest;

    private ArbeidsforholdRespons arbeidsforholdRespons;

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    void setUp() {
        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
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
    void opprettArbeidsforhold() throws JsonProcessingException {
        stubOpprettArbeidsforhold(arbeidsforholdRespons);
        var response = aaregConsumer.opprettArbeidsforhold(opprettRequest, MILJOE)
                .collectList()
                .block();
        assertThat(response)
                .isNotNull()
                .extracting(List::getFirst)
                .extracting(ArbeidsforholdRespons::getArbeidsforholdId, ArbeidsforholdRespons::getMiljo)
                .containsExactly("1", MILJOE);
    }

    @Test
    void oppdaterArbeidsforhold() throws JsonProcessingException {
        stubOppdaterArbeidsforhold(arbeidsforholdRespons);
        var response = aaregConsumer.opprettArbeidsforhold(opprettRequest, MILJOE)
                .collectList()
                .block();
        assertThat(response)
                .isNotNull()
                .extracting(List::getFirst)
                .extracting(ArbeidsforholdRespons::getArbeidsforholdId, ArbeidsforholdRespons::getMiljo)
                .containsExactly("1", MILJOE);
    }

    @Test
    void hentArbeidsforhold() throws JsonProcessingException {
        stubHentArbeidsforhold(arbeidsforholdRespons);
        var arbeidsforholdResponses = aaregConsumer.hentArbeidsforhold(IDENT, MILJOE)
                .block();
        assertThat(arbeidsforholdResponses)
                .isNotNull()
                .extracting(ArbeidsforholdRespons::getEksisterendeArbeidsforhold)
                .isEqualTo(emptyList());
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