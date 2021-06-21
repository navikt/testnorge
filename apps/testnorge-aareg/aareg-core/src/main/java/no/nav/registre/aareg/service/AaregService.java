package no.nav.registre.aareg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.consumer.rs.AaregRestConsumer;
import no.nav.registre.aareg.consumer.ws.AaregWsConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.domain.RsAktoerPerson;
import no.nav.registre.aareg.domain.RsArbeidsavtale;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsFartoy;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPeriode;
import no.nav.registre.aareg.domain.RsPersonAareg;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Organisasjon;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.getLocalDateTimeFromLocalDate;

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
                mapArbeidsforhold(navCallId, resultMap, environment, arbeidsforholdResponse);
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

    private void mapArbeidsforhold(String navCallId, Map<String, String> resultMap, String environment, ResponseEntity<List<Arbeidsforhold>> arbeidsforholdResponse) {
        if (arbeidsforholdResponse != null && arbeidsforholdResponse.getBody() != null) {
            var responseBody = arbeidsforholdResponse.getBody();
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
                                .arbeidstaker(RsPersonAareg.builder()
                                        .ident(forhold.getArbeidstaker().getOffentligIdent())
                                        .build())
                                .ansettelsesPeriode(RsPeriode.builder()
                                        .fom(getLocalDateTimeFromLocalDate(forhold.getAnsettelsesperiode().getPeriode().getFom()))
                                        .fom(getLocalDateTimeFromLocalDate(forhold.getAnsettelsesperiode().getPeriode().getTom()))
                                        .build())
                                .arbeidsavtale(RsArbeidsavtale.builder()
                                        .yrke(forhold.getArbeidsavtaler().get(0).getYrke())
                                        .stillingsprosent(0.0)
                                        .endringsdatoStillingsprosent(
                                                getLocalDateTimeFromLocalDate(forhold.getAnsettelsesperiode().getPeriode().getFom()))
                                        .build())
                                .fartoy(List.of(RsFartoy.builder()
                                        .build()))
                                .build();
                        resultMap.putAll(aaregWsConsumer.oppdaterArbeidsforhold(buildRequest(arbeidsforhold, environment, navCallId)));
                    }
            ));
        }
    }

    private static RsAaregOppdaterRequest buildRequest(
            RsArbeidsforhold arbfInput,
            String env,
            String arkivreferanse
    ) {
        var request = new RsAaregOppdaterRequest();
        request.setRapporteringsperiode(now());
        request.setArbeidsforhold(arbfInput);
        request.setEnvironments(singletonList(env));
        request.setArkivreferanse(arkivreferanse);
        return request;
    }
}
