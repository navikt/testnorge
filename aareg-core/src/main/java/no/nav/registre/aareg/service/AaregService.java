package no.nav.registre.aareg.service;

import static no.nav.registre.aareg.service.AaregAbstractClient.buildRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.consumer.rs.AaregRestConsumer;
import no.nav.registre.aareg.consumer.ws.AaregWsConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.domain.RsAktoerPerson;
import no.nav.registre.aareg.domain.RsArbeidsavtale;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPeriode;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Organisasjon;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Person;

@Slf4j
@Service
@RequiredArgsConstructor
public class AaregService {

    private final AaregWsConsumer aaregWsConsumer;
    private final AaregRestConsumer aaregRestConsumer;

    public RsAaregResponse opprettArbeidsforhold(
            RsAaregOpprettRequest request
    ) {
        return aaregWsConsumer.opprettArbeidsforhold(request);
    }

    public Map<String, String> oppdaterArbeidsforhold(
            RsAaregOppdaterRequest request
    ) {
        return aaregWsConsumer.oppdaterArbeidsforhold(request);
    }

    public ResponseEntity<List<Arbeidsforhold>> hentArbeidsforhold(
            String ident,
            String miljoe
    ) {
        return aaregRestConsumer.hentArbeidsforhold(ident, miljoe);
    }

    public RsAaregResponse slettArbeidsforhold(
            String ident,
            List<String> miljoer,
            String navCallId
    ) {
        Map<String, String> resultMap = new HashMap<>();

        miljoer.forEach(environment -> {
            try {
                ResponseEntity<List<Arbeidsforhold>> arbeidsforholdResponse = aaregRestConsumer.hentArbeidsforhold(ident, environment);
                if (arbeidsforholdResponse != null && arbeidsforholdResponse.hasBody()) {
                    var responseBody = arbeidsforholdResponse.getBody();
                    if (responseBody != null) {
                        responseBody.forEach(map -> Collections.singletonList(map).forEach(
                                forhold -> {
                                    var arbeidsforhold = RsArbeidsforhold.builder()
                                            .arbeidsforholdIDnav(forhold.getNavArbeidsforholdId())
                                            .arbeidsforholdID(forhold.getArbeidsforholdId())
                                            .arbeidsgiver("Person".equals(forhold.getArbeidsgiver().getType()) ?
                                                    RsAktoerPerson.builder()
                                                            .ident(((Person) (forhold.getArbeidsgiver())).getOffentligIdent())
                                                            .build() :
                                                    RsOrganisasjon.builder()
                                                            .orgnummer(((Organisasjon) (forhold.getArbeidsgiver())).getOrganisasjonsnummer())
                                                            .build())
                                            .arbeidsforholdstype(forhold.getType())
                                            .arbeidstaker(RsAktoerPerson.builder()
                                                    .ident(forhold.getArbeidstaker().getOffentligIdent())
                                                    .build())
                                            .ansettelsesPeriode(RsPeriode.builder()
                                                    .fom(forhold.getAnsettelsesperiode().getPeriode().getFom().atStartOfDay())
                                                    .fom(forhold.getAnsettelsesperiode().getPeriode().getTom().atStartOfDay())
                                                    .build())
                                            .arbeidsavtale(RsArbeidsavtale.builder()
                                                    .yrke(forhold.getArbeidsavtaler().get(0).getYrke())
                                                    .stillingsprosent(0.0)
                                                    .endringsdatoStillingsprosent(forhold.getAnsettelsesperiode().getPeriode().getFom().atStartOfDay())
                                                    .build())
                                            .build();
                                    resultMap.putAll(aaregWsConsumer.oppdaterArbeidsforhold(buildRequest(arbeidsforhold, environment, navCallId)));
                                }
                        ));
                    }
                }
            } catch (HttpClientErrorException e) {
                if (HttpStatus.NOT_FOUND.value() != e.getStatusCode().value()) {
                    throw e;
                }
            } catch (TestnorgeAaregFunctionalException e) {
                if (!e.getMessage().contains("Ugyldig miljø")) {
                    throw e;
                }
            }
        });
        return RsAaregResponse.builder()
                .statusPerMiljoe(resultMap)
                .build();
    }
}
