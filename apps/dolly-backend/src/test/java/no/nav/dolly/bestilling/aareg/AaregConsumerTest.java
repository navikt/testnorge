package no.nav.dolly.bestilling.aareg;

import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

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

@ExtendWith(MockitoExtension.class)
class AaregConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "01010101010";
    private static final String ORGNUMMER = "202020202";

    private static final String MILJOE = "q2";

    @Autowired
    private AaregConsumer aaregConsumer;

    private Arbeidsforhold opprettRequest;

    private Arbeidsforhold arbeidsforhold;

    private void stubOpprettArbeidsforhold(Arbeidsforhold response) {

        stubFor(post(urlPathMatching("(.*)/api/v1/arbeidsforhold"))
                .willReturn(created()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubOppdaterArbeidsforhold(Arbeidsforhold response) {

        stubFor(put(urlPathMatching("(.*)/api/v1/arbeidsforhold"))
                .willReturn(created()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubHentArbeidsforhold(Arbeidsforhold response) {

        stubFor(get(urlPathMatching("(.*)/api/v1/arbeidsforhold"))
                .withQueryParam("ident", matching(IDENT))
                .withQueryParam("miljoe", matching(MILJOE))
                .willReturn(ok()
                        .withBody(asJsonString(response))
                        .withHeader("Content-Type", "application/json")));
    }

    private static String asJsonString(final Object object) {
        return JsonMapper.builder().build().writeValueAsString(object);
    }

    @BeforeEach
    void setUp() {

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

        arbeidsforhold = Arbeidsforhold.builder()
                        .arbeidsforholdId("1")
                        .navArbeidsforholdId(123456789L)
                        .build();
    }

    @Test
    void opprettArbeidsforhold() throws Exception {

        stubOpprettArbeidsforhold(arbeidsforhold);

        StepVerifier.create(aaregConsumer.opprettArbeidsforhold(opprettRequest, MILJOE)
                .collectList())
                .assertNext(response ->
                        assertThat(response)
                                .isNotNull()
                                .extracting(List::getFirst)
                                .extracting(ArbeidsforholdRespons::getArbeidsforhold)
                                .extracting(Arbeidsforhold::getArbeidsforholdId)
                                .isEqualTo("1"))
                .verifyComplete();
    }

    @Test
    void oppdaterArbeidsforhold() throws Exception {

        stubOppdaterArbeidsforhold(arbeidsforhold);

        aaregConsumer.opprettArbeidsforhold(opprettRequest, MILJOE)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(response ->
                        assertThat(response)
                                .isNotNull()
                                .extracting(List::getFirst)
                                .extracting(ArbeidsforholdRespons::getMiljoe)
                                .isEqualTo(MILJOE))
                .verifyComplete();
    }

    @Test
    void hentArbeidsforhold() throws Exception {

        stubHentArbeidsforhold(arbeidsforhold);

        aaregConsumer.hentArbeidsforhold(IDENT, MILJOE)
                .as(StepVerifier::create)
                .assertNext(arbeidsforholdResponses ->
                        assertThat(arbeidsforholdResponses)
                                .isNotNull()
                .extracting(ArbeidsforholdRespons::getEksisterendeArbeidsforhold)
                .isEqualTo(emptyList()))
                .verifyComplete();
    }
}