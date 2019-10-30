package no.nav.registre.aareg.service;

import static java.time.LocalDate.parse;
import static no.nav.registre.aareg.service.AaregAbstractClient.buildRequest;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbeidsforholdType;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbeidsgiverType;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbforholdId;
import static no.nav.registre.aareg.service.AaregAbstractClient.getNavArbfholdId;
import static no.nav.registre.aareg.service.AaregAbstractClient.getOffentligIdent;
import static no.nav.registre.aareg.service.AaregAbstractClient.getOrgnummer;
import static no.nav.registre.aareg.service.AaregAbstractClient.getPeriodeFom;
import static no.nav.registre.aareg.service.AaregAbstractClient.getPersonnummer;
import static no.nav.registre.aareg.service.AaregAbstractClient.getYrkeskode;

import lombok.RequiredArgsConstructor;
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
import no.nav.registre.aareg.domain.RsPersonAareg;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;

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

    public ResponseEntity<List<Map>> hentArbeidsforhold(
            String ident,
            String miljoe
    ) {
        return aaregRestConsumer.hentArbeidsforhold(ident, miljoe);
    }

    public RsAaregResponse slettArbeidsforhold(
            String ident,
            List<String> miljoer
    ) {
        Map<String, String> resultMap = new HashMap<>();

        miljoer.forEach(environment -> {
            try {
                ResponseEntity<List<Map>> arbeidsforholdResponse = aaregRestConsumer.hentArbeidsforhold(ident, environment);
                if (arbeidsforholdResponse.hasBody()) {
                    List<Map> responseBody = arbeidsforholdResponse.getBody();
                    if (responseBody != null) {
                        responseBody.forEach(map -> Collections.singletonList(map).forEach(
                                forhold -> {
                                    RsArbeidsforhold arbeidsforhold = RsArbeidsforhold.builder()
                                            .arbeidsforholdIDnav(getNavArbfholdId(forhold))
                                            .arbeidsforholdID(getArbforholdId(forhold))
                                            .arbeidsgiver("Person".equals(getArbeidsgiverType(forhold)) ?
                                                    RsAktoerPerson.builder()
                                                            .ident(getPersonnummer(forhold))
                                                            .build() :
                                                    RsOrganisasjon.builder()
                                                            .orgnummer(getOrgnummer(forhold))
                                                            .build())
                                            .arbeidsforholdstype(getArbeidsforholdType(forhold))
                                            .arbeidstaker(RsPersonAareg.builder()
                                                    .ident(getOffentligIdent(forhold))
                                                    .build())
                                            .ansettelsesPeriode(RsPeriode.builder()
                                                    .fom(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .tom(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .build())
                                            .arbeidsavtale(RsArbeidsavtale.builder()
                                                    .yrke(getYrkeskode(forhold))
                                                    .stillingsprosent(0.0)
                                                    .endringsdatoStillingsprosent(parse(getPeriodeFom(forhold)).atStartOfDay())
                                                    .build())
                                            .build();
                                    resultMap.putAll(aaregWsConsumer.oppdaterArbeidsforhold(buildRequest(arbeidsforhold, environment)));
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
